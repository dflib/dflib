package org.dflib.tar.format;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Reads a Unix tar archive as an InputStream. methods are provided to position at each successive entry in the archive,
 * and then read each entry as a normal input stream using read().
 *
 * @since 2.0.0
 */
public class TarInputStream extends FilterInputStream {

    private static final int BYTE_MASK = 0xFF;
    private static final int SMALL_BUFFER_SIZE = 256;

    private final byte[] single;
    private final byte[] smallBuf;
    private final byte[] recordBuffer;
    private final int blockSize;
    private final ZipEncoding zipEncoding;
    private final List<TarStructSparse> globalSparseHeaders;
    private final boolean lenient;

    private long bytesRead;
    private boolean atEof;
    private long entrySize;
    private long entryOffset;
    private List<InputStream> sparseInputStreams;
    private int currentSparseInputStreamIndex;
    private Map<String, String> globalPaxHeaders;
    private TarEntry currentEntry;

    public TarInputStream(InputStream in) {
        this(in, TarConstants.DEFAULT_BLKSIZE, TarConstants.DEFAULT_RCDSIZE);
    }

    protected TarInputStream(InputStream in, int blockSize, int recordSize) {
        super(Objects.requireNonNull(in));
        this.zipEncoding = TarUtils.DEFAULT_ENCODING;
        this.blockSize = blockSize;
        this.recordBuffer = new byte[recordSize];
        this.lenient = false;
        this.globalSparseHeaders = new ArrayList<>();

        this.single = new byte[1];
        this.smallBuf = new byte[SMALL_BUFFER_SIZE];
        this.globalPaxHeaders = new HashMap<>();
    }

    private void applyPaxHeadersToCurrentEntry(Map<String, String> headers, List<TarStructSparse> sparseHeaders) throws IOException {
        currentEntry.updateEntryFromPaxHeaders(headers);
        currentEntry.setSparseHeaders(sparseHeaders);
    }

    /**
     * Gets the available data that can be read from the current entry in the archive. This does not indicate how much data is left in the entire archive, only
     * in the current entry. This value is determined from the entry's size header field and the amount of data already read from the current entry.
     * Integer.MAX_VALUE is returned in case more than Integer.MAX_VALUE bytes are left in the current entry in the archive.
     */
    @Override
    public int available() {
        if (isDirectory()) {
            return 0;
        }
        long available = currentEntry.getRealSize() - entryOffset;
        if (available > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        return (int) available;
    }

    /**
     * Build the input streams consisting of all-zero input streams and non-zero input streams. When reading from the non-zero input streams, the data is
     * actually read from the original input stream. The size of each input stream is introduced by the sparse headers.
     * <p>
     * NOTE : Some all-zero input streams and non-zero input streams have the size of 0. We DO NOT store the 0 size input streams because they are meaningless.
     * </p>
     */
    private void buildSparseInputStreams() throws IOException {
        currentSparseInputStreamIndex = -1;
        sparseInputStreams = new ArrayList<>();

        List<TarStructSparse> sparseHeaders = currentEntry.getOrderedSparseHeaders();

        // Stream doesn't need to be closed at all as it doesn't use any resources
        InputStream zeroInputStream = new TarSparseZeroInputStream(); // NOSONAR
        // logical offset into the extracted entry
        long offset = 0;
        for (TarStructSparse sparseHeader : sparseHeaders) {
            long zeroBlockSize = sparseHeader.getOffset() - offset;
            if (zeroBlockSize < 0) {
                // sparse header says to move backwards inside the extracted entry
                throw new IOException("Corrupted struct sparse detected");
            }

            // only store the zero block if it is not empty
            if (zeroBlockSize > 0) {
                sparseInputStreams.add(new BoundedInputStream(zeroInputStream, sparseHeader.getOffset() - offset));
            }

            // only store the input streams with non-zero size
            if (sparseHeader.getNumbytes() > 0) {
                sparseInputStreams.add(new BoundedInputStream(zeroInputStream, sparseHeader.getNumbytes()));
            }

            offset = sparseHeader.getOffset() + sparseHeader.getNumbytes();
        }
        if (!sparseInputStreams.isEmpty()) {
            currentSparseInputStreamIndex = 0;
        }
    }

    /**
     * Closes this stream. Calls the TarBuffer's close() method.
     */
    @Override
    public void close() throws IOException {

        if (sparseInputStreams != null) {
            for (InputStream inputStream : sparseInputStreams) {
                inputStream.close();
            }
        }

        in.close();
    }

    /**
     * This method is invoked once the end of the archive is hit, it tries to consume the remaining bytes under the assumption that the tool creating this
     * archive has padded the last block.
     */
    private void consumeRemainderOfLastBlock() throws IOException {
        long bytesReadOfLastBlock = bytesRead % blockSize;
        if (bytesReadOfLastBlock > 0) {
            count(TarUtils.skip(in, blockSize - bytesReadOfLastBlock, () -> new byte[8192]));
        }
    }

    /**
     * For FileInputStream, the skip always return the number you input, so we need the available bytes to determine how many bytes are actually skipped
     */
    private long getActuallySkipped(long available, long skipped, long expected) throws IOException {
        long actuallySkipped = skipped;
        if (in instanceof FileInputStream) {
            actuallySkipped = Math.min(skipped, available);
        }
        if (actuallySkipped != expected) {
            throw new IOException("Truncated TAR archive");
        }
        return actuallySkipped;
    }

    /**
     * Gets the next entry in this tar archive as long name data.
     *
     * @return The next entry in the archive as long name data, or null.
     * @throws IOException on error
     */
    protected byte[] getLongNameData() throws IOException {
        // read in the name
        final ByteArrayOutputStream longName = new ByteArrayOutputStream();
        int length;
        while ((length = read(smallBuf)) >= 0) {
            longName.write(smallBuf, 0, length);
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
     * Gets the next TarEntry in this stream.
     *
     * @return the next entry, or {@code null} if there are no more entries
     * @throws IOException if the next entry could not be read
     */
    public TarEntry getNextEntry() throws IOException {
        if (atEof) {
            return null;
        }
        if (currentEntry != null) {
            // Skip will only go to the end of the current entry
            TarUtils.skip(this, Long.MAX_VALUE, () -> new byte[8192]);
            // skip to the end of the last record
            skipRecordPadding();
        }
        byte[] headerBuf = getRecord();
        if (headerBuf == null) {
            /* hit EOF */
            currentEntry = null;
            return null;
        }
        try {
            currentEntry = new TarEntry(globalPaxHeaders, headerBuf, zipEncoding, lenient);
        } catch (IllegalArgumentException e) {
            throw new IOException("Error detected parsing the header", e);
        }
        entryOffset = 0;
        entrySize = currentEntry.getSize();
        if (currentEntry.isGnuLongLink()) {
            final byte[] longLinkData = getLongNameData();
            if (longLinkData == null) {
                // Bugzilla: 40334
                // Malformed tar file - long link entry name not followed by entry
                return null;
            }
            currentEntry.setLinkName(zipEncoding.decode(longLinkData));
        }
        if (currentEntry.isGnuLongName()) {
            final byte[] longNameData = getLongNameData();
            if (longNameData == null) {
                // Bugzilla: 40334
                // Malformed tar file - long entry name not followed by entry
                return null;
            }
            // COMPRESS-509 : the name of directories should end with '/'
            final String name = zipEncoding.decode(longNameData);
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
        // If the size of the next element in the archive has changed
        // due to a new size being reported in the POSIX header
        // information, we update entrySize here so that it contains
        // the correct value.
        entrySize = currentEntry.getSize();
        return currentEntry;
    }

    /**
     * Gets the next record in this tar archive. This will skip over any remaining data in the current entry, if there is one, and place the input stream at the
     * header of the next entry.
     * <p>
     * If there are no more entries in the archive, null will be returned to indicate that the end of the archive has been reached. At the same time the
     * {@code hasHitEOF} marker will be set to true.
     * </p>
     *
     * @return The next header in the archive, or null.
     * @throws IOException on error
     */
    private byte[] getRecord() throws IOException {
        byte[] headerBuf = readRecord();

        this.atEof = isEOFRecord(headerBuf);

        if (atEof && headerBuf != null) {
            tryToConsumeSecondEOFRecord();
            consumeRemainderOfLastBlock();
            headerBuf = null;
        }
        return headerBuf;
    }

    private int getRecordSize() {
        return recordBuffer.length;
    }

    private boolean isDirectory() {
        return currentEntry != null && currentEntry.isDirectory();
    }

    private boolean isEOFRecord(byte[] record) {
        return record == null || TarUtils.isArrayZero(record, getRecordSize());
    }

    /**
     * Since we do not support marking just yet, we do nothing.
     *
     * @param markLimit The limit to mark.
     */
    @Override
    public synchronized void mark(int markLimit) {
    }

    /**
     * Since we do not support marking just yet, we return false.
     */
    @Override
    public boolean markSupported() {
        return false;
    }

    /**
     * For PAX Format 0.0, the sparse headers(GNU.sparse.offset and GNU.sparse.numbytes) may appear multi times, and they look like:
     * <p>
     * GNU.sparse.size=size GNU.sparse.numblocks=numblocks repeat numblocks times GNU.sparse.offset=offset GNU.sparse.numbytes=numbytes end repeat
     * </p>
     * <p>
     * For PAX Format 0.1, the sparse headers are stored in a single variable : GNU.sparse.map
     * </p>
     * <p>
     * GNU.sparse.map Map of non-null data chunks. It is a string consisting of comma-separated values "offset,size[,offset-1,size-1...]"
     * </p>
     * <p>
     * For PAX Format 1.X: The sparse map itself is stored in the file data block, preceding the actual file data. It consists of a series of decimal numbers
     * delimited by newlines. The map is padded with nulls to the nearest block boundary. The first number gives the number of entries in the map. Following are
     * map entries, each one consisting of two numbers giving the offset and size of the data block it describes.
     * </p>
     *
     * @throws IOException if an I/O error occurs.
     */
    private void paxHeaders() throws IOException {
        List<TarStructSparse> sparseHeaders = new ArrayList<>();
        Map<String, String> headers = TarUtils.parsePaxHeaders(this, sparseHeaders, globalPaxHeaders, entrySize);
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
            sparseHeaders = TarUtils.parsePAX1XSparseHeaders(in, getRecordSize());
            currentEntry.setSparseHeaders(sparseHeaders);
        }
        // sparse headers are all done reading, we need to build
        // sparse input streams using these sparse headers
        buildSparseInputStreams();
    }

    @Override
    public int read() throws IOException {
        final int num = read(single, 0, 1);
        return num == -1 ? -1 : single[0] & BYTE_MASK;
    }

    /**
     * Reads bytes from the current tar archive entry. This method is aware of the boundaries of the current entry in
     * the archive and will deal with them as if they were this stream's start and EOF.
     */
    @Override
    public int read(byte[] buf, int offset, int numToRead) throws IOException {
        if (numToRead == 0) {
            return 0;
        }
        int totalRead;
        if (atEof || isDirectory()) {
            return -1;
        }
        if (currentEntry == null) {
            throw new IllegalStateException("No current tar entry");
        }

        if (entryOffset >= currentEntry.getRealSize()) {
            return -1;
        }

        numToRead = Math.min(numToRead, available());
        if (currentEntry.isSparse()) {
            // for sparse entries, we need to read them in another way
            totalRead = readSparse(buf, offset, numToRead);
        } else {
            totalRead = in.read(buf, offset, numToRead);
        }
        if (totalRead == -1) {
            if (numToRead > 0) {
                throw new IOException("Truncated TAR archive");
            }
            this.atEof = true;
        } else {
            count(totalRead);
            entryOffset += totalRead;
        }
        return totalRead;
    }

    private void readGlobalPaxHeaders() throws IOException {
        globalPaxHeaders = TarUtils.parsePaxHeaders(this, globalSparseHeaders, globalPaxHeaders, entrySize);
        getNextEntry(); // Get the actual file entry
        if (currentEntry == null) {
            throw new IOException("Error detected parsing the pax header");
        }
    }

    /**
     * Adds the sparse chunks from the current entry to the sparse chunks, including any additional sparse entries following the current entry.
     */
    private void readOldGNUSparse() throws IOException {
        if (currentEntry.isExtended()) {
            TarSparseEntry entry;
            do {
                final byte[] headerBuf = getRecord();
                if (headerBuf == null) {
                    throw new IOException("premature end of tar archive. Didn't find extended_header after header with extended flag.");
                }
                entry = new TarSparseEntry(headerBuf);
                currentEntry.getSparseHeaders().addAll(entry.getSparseHeaders());
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
     * @throws IOException on error
     */
    protected byte[] readRecord() throws IOException {
        int readCount = TarUtils.readFully(in, recordBuffer, 0, recordBuffer.length);
        count(readCount);
        if (readCount != getRecordSize()) {
            return null;
        }
        return recordBuffer;
    }

    /**
     * For sparse tar entries, there are many "holes"(consisting of all 0) in the file. Only the non-zero data is stored in tar files, and they are stored
     * separately. The structure of non-zero data is introduced by the sparse headers using the offset, where a block of non-zero data starts, and numbytes, the
     * length of the non-zero data block. When reading sparse entries, the actual data is read out with "holes" and non-zero data combined together according to
     * the sparse headers.
     *
     * @param buf       The buffer into which to place bytes read.
     * @param offset    The offset at which to place bytes read.
     * @param numToRead The number of bytes to read.
     * @return The number of bytes read, or -1 at EOF.
     * @throws IOException on error
     */
    private int readSparse(byte[] buf, int offset, int numToRead) throws IOException {
        // if there are no actual input streams, just read from the original input stream
        if (sparseInputStreams == null || sparseInputStreams.isEmpty()) {
            return in.read(buf, offset, numToRead);
        }
        if (currentSparseInputStreamIndex >= sparseInputStreams.size()) {
            return -1;
        }
        InputStream currentInputStream = sparseInputStreams.get(currentSparseInputStreamIndex);
        final int readLen = currentInputStream.read(buf, offset, numToRead);
        // if the current input stream is the last input stream,
        // just return the number of bytes read from current input stream
        if (currentSparseInputStreamIndex == sparseInputStreams.size() - 1) {
            return readLen;
        }
        // if EOF of current input stream is meet, open a new input stream and recursively call read
        if (readLen == -1) {
            currentSparseInputStreamIndex++;
            return readSparse(buf, offset, numToRead);
        }
        // if the rest data of current input stream is not long enough, open a new input stream
        // and recursively call read
        if (readLen < numToRead) {
            currentSparseInputStreamIndex++;
            final int readLenOfNext = readSparse(buf, offset + readLen, numToRead - readLen);
            if (readLenOfNext == -1) {
                return readLen;
            }
            return readLen + readLenOfNext;
        }
        // if the rest data of current input stream is enough(which means readLen == len), just return readLen
        return readLen;
    }

    /**
     * Since we do not support marking just yet, we do nothing.
     */
    @Override
    public void reset() {
        // empty
    }

    /**
     * Skips over and discards {@code n} bytes of data from this input stream. The {@code skip} method may, for a variety of reasons, end up skipping over some
     * smaller number of bytes, possibly {@code 0}. This may result from any of a number of conditions; reaching end of file or end of entry before {@code n}
     * bytes have been skipped; are only two possibilities. The actual number of bytes skipped is returned. If {@code n} is negative, no bytes are skipped.
     */
    @Override
    public long skip(long n) throws IOException {
        if (n <= 0 || isDirectory()) {
            return 0;
        }
        final long availableOfInputStream = in.available();
        final long available = currentEntry.getRealSize() - entryOffset;
        final long numToSkip = Math.min(n, available);
        long skipped;
        if (!currentEntry.isSparse()) {
            skipped = TarUtils.skip(in, numToSkip, () -> new byte[8192]);
            // for non-sparse entry, we should get the bytes actually skipped bytes along with
            // inputStream.available() if inputStream is instance of FileInputStream
            skipped = getActuallySkipped(availableOfInputStream, skipped, numToSkip);
        } else {
            skipped = skipSparse(numToSkip);
        }
        count(skipped);
        entryOffset += skipped;
        return skipped;
    }

    /**
     * The last record block should be written at the full size, so skip any additional space used to fill a record after an entry.
     *
     * @throws IOException if a truncated tar archive is detected
     */
    private void skipRecordPadding() throws IOException {
        if (!isDirectory() && this.entrySize > 0 && this.entrySize % getRecordSize() != 0) {
            long available = in.available();
            long numRecords = this.entrySize / getRecordSize() + 1;
            long padding = numRecords * getRecordSize() - this.entrySize;
            long skipped = TarUtils.skip(in, padding, () -> new byte[8192]);
            skipped = getActuallySkipped(available, skipped, padding);
            count(skipped);
        }
    }

    /**
     * Skip n bytes from current input stream, if the current input stream doesn't have enough data to skip, jump to the next input stream and skip the rest
     * bytes, keep doing this until total n bytes are skipped or the input streams are all skipped
     *
     * @param n bytes of data to skip
     * @return actual bytes of data skipped
     * @throws IOException if an I/O error occurs.
     */
    private long skipSparse(long n) throws IOException {
        if (sparseInputStreams == null || sparseInputStreams.isEmpty()) {
            return in.skip(n);
        }
        long bytesSkipped = 0;
        while (bytesSkipped < n && currentSparseInputStreamIndex < sparseInputStreams.size()) {
            final InputStream currentInputStream = sparseInputStreams.get(currentSparseInputStreamIndex);
            bytesSkipped += currentInputStream.skip(n - bytesSkipped);
            if (bytesSkipped < n) {
                currentSparseInputStreamIndex++;
            }
        }
        return bytesSkipped;
    }

    /**
     * Tries to read the next record rewinding the stream if it is not an EOF record.
     * <p>
     * This is meant to protect against cases where a tar implementation has written only one EOF record when two are expected. Actually this won't help since a
     * non-conforming implementation likely won't fill full blocks consisting of - by default - ten records either so we probably have already read beyond the
     * archive anyway.
     * </p>
     */
    private void tryToConsumeSecondEOFRecord() throws IOException {
        boolean shouldReset = true;
        final boolean marked = in.markSupported();
        if (marked) {
            in.mark(getRecordSize());
        }
        try {
            shouldReset = !isEOFRecord(readRecord());
        } finally {
            if (shouldReset && marked) {
                pushedBackBytes(getRecordSize());
                in.reset();
            }
        }
    }

    private void pushedBackBytes(long pushedBack) {
        bytesRead -= pushedBack;
    }

    private void count(long read) {
        if (read != -1) {
            bytesRead += read;
        }
    }
}
