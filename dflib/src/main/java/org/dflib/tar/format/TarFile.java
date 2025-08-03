package org.dflib.tar.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: unused, work in progress. If we can make this class reentrant,
//  we should be able to implement an efficient RandomAccessTar
class TarFile implements AutoCloseable {

    private final byte[] smallBuf = new byte[256];
    private final ZipEncoding zipEncoding;

    private final SeekableByteChannel channel;
    private final List<TarEntry> entries;
    private final int blockSize;
    private final boolean lenient;
    private final int recordSize;
    private final ByteBuffer recordBuffer;
    // the global sparse headers, this is only used in PAX Format 0.X
    private final List<TarStructSparse> globalSparseHeaders;

    private boolean eof;
    private TarEntry currentEntry;
    private Map<String, String> globalPaxHeaders;
    private final Map<String, List<InputStream>> sparseInputStreams;

    public TarFile(Path archivePath) throws IOException {
        this(Files.newByteChannel(archivePath), TarConstants.DEFAULT_BLKSIZE, TarConstants.DEFAULT_RCDSIZE, Charset.defaultCharset(), false);
    }

    TarFile(SeekableByteChannel channel, int blockSize, int recordSize, Charset encoding, boolean lenient) throws IOException {
        this.channel = channel;
        this.zipEncoding = new ZipEncoding(encoding);
        this.recordSize = recordSize;
        this.recordBuffer = ByteBuffer.allocate(this.recordSize);
        this.blockSize = blockSize;
        this.lenient = lenient;

        this.globalPaxHeaders = new HashMap<>();
        this.globalSparseHeaders = new ArrayList<>();
        this.sparseInputStreams = new HashMap<>();
        this.entries = new ArrayList<>();

        TarEntry entry;
        while ((entry = getNextEntry()) != null) {
            entries.add(entry);
        }
    }

    /**
     * Update the current entry with the read pax headers
     */
    private void applyPaxHeadersToCurrentEntry(Map<String, String> headers, List<TarStructSparse> sparseHeaders) throws IOException {
        currentEntry.updateEntryFromPaxHeaders(headers);
        currentEntry.setSparseHeaders(sparseHeaders);
    }

    /**
     * Build the input streams consisting of all-zero input streams and non-zero input streams. When reading from the
     * non-zero input streams, the data is actually read from the original input stream. The size of each input stream
     * is introduced by the sparse headers. Some all-zero input streams and non-zero input streams have the size of 0.
     * We DO NOT store the 0 size input streams because they are meaningless.
     */
    private void buildSparseInputStreams() throws IOException {
        List<InputStream> streams = new ArrayList<>();
        List<TarStructSparse> sparseHeaders = currentEntry.getOrderedSparseHeaders();
        // Stream doesn't need to be closed at all as it doesn't use any resources
        InputStream zeroInputStream = new TarSparseZeroInputStream(); // NOSONAR
        // logical offset into the extracted entry
        long offset = 0;
        long numberOfZeroBytesInSparseEntry = 0;
        for (TarStructSparse sparseHeader : sparseHeaders) {
            long zeroBlockSize = sparseHeader.getOffset() - offset;
            if (zeroBlockSize < 0) {
                // sparse header says to move backwards inside the extracted entry
                throw new IOException("Corrupted struct sparse detected");
            }
            // only store the zero block if it is not empty
            if (zeroBlockSize > 0) {
                streams.add(new BoundedInputStream(zeroInputStream, zeroBlockSize));
                numberOfZeroBytesInSparseEntry += zeroBlockSize;
            }
            // only store the input streams with non-zero size
            if (sparseHeader.getNumbytes() > 0) {
                long start = currentEntry.getDataOffset() + sparseHeader.getOffset() - numberOfZeroBytesInSparseEntry;
                if (start + sparseHeader.getNumbytes() < start) {
                    // possible integer overflow
                    throw new IOException("Unreadable TAR archive, sparse block offset or length too big");
                }
                streams.add(new BoundedSeekableByteChannelInputStream(start, sparseHeader.getNumbytes(), channel));
            }
            offset = sparseHeader.getOffset() + sparseHeader.getNumbytes();
        }
        sparseInputStreams.put(currentEntry.getName(), streams);
    }

    @Override
    public void close() throws IOException {
        channel.close();
    }

    /**
     * This method is invoked once the end of the archive is hit, it tries to consume the remaining bytes under the assumption that the tool creating this
     * archive has padded the last block.
     */
    private void consumeRemainderOfLastBlock() throws IOException {
        final long bytesReadOfLastBlock = channel.position() % blockSize;
        if (bytesReadOfLastBlock > 0) {
            repositionForwardBy(blockSize - bytesReadOfLastBlock);
        }
    }

    /**
     * Gets all TAR Archive Entries from the TarFile
     *
     * @return All entries from the tar file
     */
    public List<TarEntry> getEntries() {
        return entries;
    }

    /**
     * Gets the input stream for the provided Tar Archive Entry.
     */
    public InputStream getInputStream(TarEntry entry) throws IOException {
        try {
            return new BoundedTarEntryInputStream(entry, channel);
        } catch (RuntimeException ex) {
            throw new IOException("Corrupted TAR archive. Can't read entry", ex);
        }
    }

    /**
     * Gets the next entry in this tar archive as long name data.
     */
    private byte[] getLongNameData() throws IOException {
        final ByteArrayOutputStream longName = new ByteArrayOutputStream();
        int length;
        try (InputStream in = getInputStream(currentEntry)) {
            while ((length = in.read(smallBuf)) >= 0) {
                longName.write(smallBuf, 0, length);
            }
        }
        getNextEntry();
        if (currentEntry == null) {
            // Bugzilla: 40334
            // Malformed tar file - long entry name not followed by entry
            return null;
        }
        byte[] longNameData = longName.toByteArray();
        // remove trailing null terminator(s)
        length = longNameData.length;
        while (length > 0 && longNameData[length - 1] == 0) {
            --length;
        }
        if (length != longNameData.length) {
            longNameData = Arrays.copyOf(longNameData, length);
        }
        return longNameData;
    }

    /**
     * Gets the next entry in this tar archive. This will skip to the end of the current entry if there is one, and
     * place the position of the channel at the header of the next entry, and read the header and instantiate a new
     * TarEntry from the header bytes and return that entry. If there are no more entries in the archive, null will be
     * returned to indicate that the end of the archive has been reached.
     */
    private TarEntry getNextEntry() throws IOException {
        if (isAtEOF()) {
            return null;
        }

        if (currentEntry != null) {
            // Skip to the end of the entry
            repositionForwardTo(currentEntry.getDataOffset() + currentEntry.getSize());
            throwIfPositionIsNotInArchive();
            skipRecordPadding();
        }

        ByteBuffer headerBuf = getRecord();
        if (null == headerBuf) {
            // Hit EOF
            currentEntry = null;
            return null;
        }

        try {
            currentEntry = new TarEntry(globalPaxHeaders, headerBuf.array(), zipEncoding, lenient, channel.position());
        } catch (IllegalArgumentException e) {
            throw new IOException("Error detected parsing the header", e);
        }

        if (currentEntry.isGnuLongLink()) {
            byte[] longLinkData = getLongNameData();
            if (longLinkData == null) {
                // Bugzilla: 40334
                // Malformed tar file - long link entry name not followed by
                // entry
                return null;
            }
            currentEntry.setLinkName(zipEncoding.decode(longLinkData));
        }

        if (currentEntry.isGnuLongName()) {
            byte[] longNameData = getLongNameData();
            if (longNameData == null) {
                // Bugzilla: 40334
                // Malformed tar file - long entry name not followed by
                // entry
                return null;
            }

            // the name of directories should end with '/'
            String name = zipEncoding.decode(longNameData);
            currentEntry.setName(name);
            if (currentEntry.isDirectory() && !name.endsWith("/")) {
                currentEntry.setName(name + "/");
            }
        }

        if (currentEntry.isGlobalPaxHeader()) { // Process Global Pax headers
            readGlobalPaxHeaders();
        }

        try {
            if (currentEntry.isPaxHeader()) { // Process Pax headers
                paxHeaders();
            } else if (!globalPaxHeaders.isEmpty()) {
                applyPaxHeadersToCurrentEntry(globalPaxHeaders, globalSparseHeaders);
            }
        } catch (NumberFormatException e) {
            throw new IOException("Error detected parsing the pax header", e);
        }

        if (currentEntry.isOldGnuSparse()) { // Process sparse files
            readOldGNUSparse();
        }

        return currentEntry;
    }

    /**
     * Gets the next record in this tar archive. This will skip over any remaining data in the current entry, if there
     * is one, and place the input stream at the header of the next entry. If there are no more entries in the archive,
     * null will be returned to indicate that the end of the archive has been reached. At the same time the
     * "hasHitEOF" marker will be set to true.
     * </p>
     */
    private ByteBuffer getRecord() throws IOException {
        ByteBuffer headerBuf = readRecord();
        setAtEOF(isEOFRecord(headerBuf));
        if (isAtEOF() && headerBuf != null) {
            // Consume rest
            tryToConsumeSecondEOFRecord();
            consumeRemainderOfLastBlock();
            headerBuf = null;
        }
        return headerBuf;
    }

    /**
     * Tests whether or not we are at the end-of-file.
     */
    protected final boolean isAtEOF() {
        return eof;
    }

    private boolean isDirectory() {
        return currentEntry != null && currentEntry.isDirectory();
    }

    private boolean isEOFRecord(ByteBuffer headerBuf) {
        return headerBuf == null || TarUtils.isArrayZero(headerBuf.array(), recordSize);
    }

    /**
     * <p>
     * For PAX Format 0.0, the sparse headers(GNU.sparse.offset and GNU.sparse.numbytes) may appear multi times, and they look like:
     *
     * <pre>
     * GNU.sparse.size=size
     * GNU.sparse.numblocks=numblocks
     * repeat numblocks times
     *   GNU.sparse.offset=offset
     *   GNU.sparse.numbytes=numbytes
     * end repeat
     * </pre>
     *
     * <p>
     * For PAX Format 0.1, the sparse headers are stored in a single variable : GNU.sparse.map
     *
     * <pre>
     * GNU.sparse.map
     *    Map of non-null data chunks. It is a string consisting of comma-separated values "offset,size[,offset-1,size-1...]"
     * </pre>
     *
     * <p>
     * For PAX Format 1.X: <br>
     * The sparse map itself is stored in the file data block, preceding the actual file data. It consists of a series of decimal numbers delimited by newlines.
     * The map is padded with nulls to the nearest block boundary. The first number gives the number of entries in the map. Following are map entries, each one
     * consisting of two numbers giving the offset and size of the data block it describes.
     */
    private void paxHeaders() throws IOException {
        List<TarStructSparse> sparseHeaders = new ArrayList<>();
        Map<String, String> headers;
        try (InputStream input = getInputStream(currentEntry)) {
            headers = TarUtils.parsePaxHeaders(input, sparseHeaders, globalPaxHeaders, currentEntry.getSize());
        }

        // for 0.1 PAX Headers
        if (headers.containsKey(TarGnuSparseKeys.MAP)) {
            sparseHeaders = new ArrayList<>(TarUtils.parseFromPAX01SparseHeaders(headers.get(TarGnuSparseKeys.MAP)));
        }
        getNextEntry(); // Get the actual file entry
        if (currentEntry == null) {
            throw new IOException("premature end of tar archive. Didn't find any entry after PAX header.");
        }
        applyPaxHeadersToCurrentEntry(headers, sparseHeaders);

        // for 1.0 PAX Format, the sparse map is stored in the file data block
        if (currentEntry.isPaxGnu1XSparse()) {
            try (InputStream input = getInputStream(currentEntry)) {
                sparseHeaders = TarUtils.parsePAX1XSparseHeaders(input, recordSize);
            }
            currentEntry.setSparseHeaders(sparseHeaders);
            // data of the entry is after the pax gnu entry. So we need to update the data position once again
            currentEntry.setDataOffset(currentEntry.getDataOffset() + recordSize);
        }

        // sparse headers are all done reading, we need to build
        // sparse input streams using these sparse headers
        buildSparseInputStreams();
    }

    private void readGlobalPaxHeaders() throws IOException {
        try (InputStream input = getInputStream(currentEntry)) {
            globalPaxHeaders = TarUtils.parsePaxHeaders(input, globalSparseHeaders, globalPaxHeaders, currentEntry.getSize());
        }
        getNextEntry(); // Get the actual file entry

        if (currentEntry == null) {
            throw new IOException("Error detected parsing the pax header");
        }
    }

    /**
     * Adds the sparse chunks from the current entry to the sparse chunks, including any additional sparse entries following the current entry.
     *
     * @throws IOException when reading the sparse entry fails
     */
    private void readOldGNUSparse() throws IOException {
        if (currentEntry.isExtended()) {
            TarSparseEntry entry;
            do {
                final ByteBuffer headerBuf = getRecord();
                if (headerBuf == null) {
                    throw new IOException("premature end of tar archive. Didn't find extended_header after header with extended flag.");
                }
                entry = new TarSparseEntry(headerBuf.array());
                currentEntry.getSparseHeaders().addAll(entry.getSparseHeaders());
                currentEntry.setDataOffset(currentEntry.getDataOffset() + recordSize);
            } while (entry.isExtended());
        }

        // sparse headers are all done reading, we need to build
        // sparse input streams using these sparse headers
        buildSparseInputStreams();
    }

    /**
     * Reads a record from the input stream and return the data.
     *
     * @return The record data or null if EOF has been hit.
     * @throws IOException if reading from the archive fails
     */
    private ByteBuffer readRecord() throws IOException {
        recordBuffer.rewind();
        final int readNow = channel.read(recordBuffer);
        if (readNow != recordSize) {
            return null;
        }
        return recordBuffer;
    }

    private void repositionForwardBy(long offset) throws IOException {
        repositionForwardTo(channel.position() + offset);
    }

    private void repositionForwardTo(long newPosition) throws IOException {
        final long currPosition = channel.position();
        if (newPosition < currPosition) {
            throw new IOException("trying to move backwards inside of the archive");
        }
        channel.position(newPosition);
    }

    /**
     * Sets whether we are at end-of-file.
     *
     * @param eof whether we are at end-of-file.
     */
    protected final void setAtEOF(boolean eof) {
        this.eof = eof;
    }

    /**
     * The last record block should be written at the full size, so skip any additional space used to fill a record after an entry
     *
     * @throws IOException when skipping the padding of the record fails
     */
    private void skipRecordPadding() throws IOException {
        if (!isDirectory() && currentEntry.getSize() > 0 && currentEntry.getSize() % recordSize != 0) {
            final long numRecords = currentEntry.getSize() / recordSize + 1;
            final long padding = numRecords * recordSize - currentEntry.getSize();
            repositionForwardBy(padding);
            throwIfPositionIsNotInArchive();
        }
    }

    /**
     * Checks if the current position of the SeekableByteChannel is in the archive.
     */
    private void throwIfPositionIsNotInArchive() throws IOException {
        if (channel.size() < channel.position()) {
            throw new IOException("Truncated TAR archive");
        }
    }

    /**
     * Tries to read the next record resetting the position in the archive if it is not an EOF record. Meant to protect
     * against cases where a tar implementation has written only one EOF record when two are expected. Actually, this
     * won't help since a non-conforming implementation likely won't fill full blocks consisting of - by default - ten
     * records either so we probably have already read beyond the archive anyway.
     */
    private void tryToConsumeSecondEOFRecord() throws IOException {
        boolean shouldReset = true;
        try {
            shouldReset = !isEOFRecord(readRecord());
        } finally {
            if (shouldReset) {
                channel.position(channel.position() - recordSize);
            }
        }
    }

    private final class BoundedTarEntryInputStream extends BoundedArchiveInputStream {

        private final SeekableByteChannel channel;

        private final TarEntry entry;
        private long entryOffset;
        private int currentSparseInputStreamIndex;

        BoundedTarEntryInputStream(TarEntry entry, SeekableByteChannel channel) throws IOException {
            super(entry.getDataOffset(), entry.getRealSize());
            if (channel.size() - entry.getSize() < entry.getDataOffset()) {
                throw new IOException("entry size exceeds archive size");
            }
            this.entry = entry;
            this.channel = channel;
        }

        @Override
        protected int read(long pos, ByteBuffer buf) throws IOException {
            if (entryOffset >= entry.getRealSize()) {
                return -1;
            }

            final int totalRead;
            if (entry.isSparse()) {
                totalRead = readSparse(entryOffset, buf, buf.limit());
            } else {
                totalRead = readArchive(pos, buf);
            }

            if (totalRead == -1) {
                if (buf.array().length > 0) {
                    throw new IOException("Truncated TAR archive");
                }
                setAtEOF(true);
            } else {
                entryOffset += totalRead;
                buf.flip();
            }
            return totalRead;
        }

        private int readArchive(long pos, ByteBuffer buf) throws IOException {
            channel.position(pos);
            return channel.read(buf);
        }

        private int readSparse(long pos, ByteBuffer buf, int numToRead) throws IOException {
            // if there are no actual input streams, just read from the original archive
            List<InputStream> entrySparseInputStreams = sparseInputStreams.get(entry.getName());
            if (entrySparseInputStreams == null || entrySparseInputStreams.isEmpty()) {
                return readArchive(entry.getDataOffset() + pos, buf);
            }

            if (currentSparseInputStreamIndex >= entrySparseInputStreams.size()) {
                return -1;
            }

            InputStream currentInputStream = entrySparseInputStreams.get(currentSparseInputStreamIndex);
            byte[] bufArray = new byte[numToRead];
            int readLen = currentInputStream.read(bufArray);
            if (readLen != -1) {
                buf.put(bufArray, 0, readLen);
            }

            // if the current input stream is the last input stream,
            // just return the number of bytes read from current input stream
            if (currentSparseInputStreamIndex == entrySparseInputStreams.size() - 1) {
                return readLen;
            }

            // if EOF of current input stream is meet, open a new input stream and recursively call read
            if (readLen == -1) {
                currentSparseInputStreamIndex++;
                return readSparse(pos, buf, numToRead);
            }

            // if the rest data of current input stream is not long enough, open a new input stream
            // and recursively call read
            if (readLen < numToRead) {
                currentSparseInputStreamIndex++;
                final int readLenOfNext = readSparse(pos + readLen, buf, numToRead - readLen);
                if (readLenOfNext == -1) {
                    return readLen;
                }

                return readLen + readLenOfNext;
            }

            // if the rest data of the current input stream is enough (which means readLen == len), just return readLen
            return readLen;
        }
    }
}
