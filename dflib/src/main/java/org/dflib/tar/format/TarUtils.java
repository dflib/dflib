package org.dflib.tar.format;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static java.nio.charset.StandardCharsets.US_ASCII;

/**
 * Provides static utility methods to work with TAR streams.
 *
 * @since 2.0.0
 */
class TarUtils {

    private static final BigInteger NEG_1_BIG_INT = BigInteger.valueOf(-1);
    static final ZipEncoding DEFAULT_ENCODING = new ZipEncoding(Charset.defaultCharset());

    /*
     * Generates an exception message.
     */
    private static String exceptionMessage(byte[] buffer, int offset, int length, int current, byte currentByte) {
        // default charset is good enough for an exception message,
        //
        // the alternative was to modify parseOctal and
        // parseOctalOrBinary to receive the ZipEncoding of the
        // archive (deprecating the existing public methods, of
        // course) and dealing with the fact that ZipEncoding#decode
        // can throw an IOException which parseOctal* doesn't declare
        String string = new String(buffer, offset, length, Charset.defaultCharset());
        string = string.replace("\0", "{NUL}"); // Replace NULs to allow string to be printed
        return "Invalid byte " + currentByte + " at offset " + (current - offset) + " in '" + string + "' len=" + length;
    }

    private static long parseBinaryBigInteger(byte[] buffer, int offset, int length, boolean negative) {
        final byte[] remainder = new byte[length - 1];
        System.arraycopy(buffer, offset + 1, remainder, 0, length - 1);
        BigInteger val = new BigInteger(remainder);
        if (negative) {
            // 2's complement
            val = val.add(NEG_1_BIG_INT).not();
        }
        if (val.bitLength() > 63) {
            throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number exceeds maximum signed long value");
        }
        return negative ? -val.longValue() : val.longValue();
    }

    private static long parseBinaryLong(byte[] buffer, int offset, int length, boolean negative) {
        if (length >= 9) {
            throw new IllegalArgumentException("At offset " + offset + ", " + length + " byte binary number exceeds maximum signed long value");
        }
        long val = 0;
        for (int i = 1; i < length; i++) {
            val = (val << 8) + (buffer[offset + i] & 0xff);
        }
        if (negative) {
            // 2's complement
            val--;
            val ^= (long) Math.pow(2.0, (length - 1) * 8.0) - 1;
        }
        return negative ? -val : val;
    }

    /**
     * Parses a boolean byte from a buffer. Leading spaces and NUL are ignored. The buffer may contain trailing spaces or NULs.
     */
    public static boolean parseBoolean(byte[] buffer, int offset) {
        return buffer[offset] == 1;
    }

    /**
     * For PAX Format 0.1, the sparse headers are stored in a single variable : GNU.sparse.map GNU.sparse.map Map of non-null data chunks. It is a string
     * consisting of comma-separated values "offset,size[,offset-1,size-1...]"
     */
    public static List<TarStructSparse> parseFromPAX01SparseHeaders(String sparseMap) throws IOException {
        final List<TarStructSparse> sparseHeaders = new ArrayList<>();
        final String[] sparseHeaderStrings = sparseMap.split(",");
        if (sparseHeaderStrings.length % 2 == 1) {
            throw new IOException("Corrupted TAR archive. Bad format in GNU.sparse.map PAX Header");
        }
        for (int i = 0; i < sparseHeaderStrings.length; i += 2) {
            final long sparseOffset = parseLongValue(sparseHeaderStrings[i]);
            if (sparseOffset < 0) {
                throw new IOException("Corrupted TAR archive. Sparse struct offset contains negative value");
            }
            final long sparseNumbytes = parseLongValue(sparseHeaderStrings[i + 1]);
            if (sparseNumbytes < 0) {
                throw new IOException("Corrupted TAR archive. Sparse struct numbytes contains negative value");
            }
            sparseHeaders.add(new TarStructSparse(sparseOffset, sparseNumbytes));
        }
        return Collections.unmodifiableList(sparseHeaders);
    }

    /**
     * Parses an entry name from a buffer. Parsing stops when a NUL is found or the buffer length is reached.
     */
    public static String parseName(byte[] buffer, int offset, int length, ZipEncoding encoding) throws IOException {
        int len = 0;
        for (int i = offset; len < length && buffer[i] != 0; i++) {
            len++;
        }
        if (len > 0) {
            final byte[] b = new byte[len];
            System.arraycopy(buffer, offset, b, 0, len);
            return encoding.decode(b);
        }
        return "";
    }

    /**
     * Parses an octal string from a buffer. Leading spaces are ignored. The buffer must contain a trailing space or
     * NUL, and may contain an additional trailing space or NUL. The input buffer is allowed to contain all NULs, in
     * which case the method returns 0L (this allows for missing fields). To work around some tar implementations that
     * insert a leading NUL this method returns 0 if it detects a leading NUL.
     */
    public static long parseOctal(byte[] buffer, int offset, int length) {
        long result = 0;
        int end = offset + length;
        int start = offset;
        if (length < 2) {
            throw new IllegalArgumentException("Length " + length + " must be at least 2");
        }
        if (buffer[start] == 0) {
            return 0L;
        }
        // Skip leading spaces
        while (start < end) {
            if (buffer[start] != ' ') {
                break;
            }
            start++;
        }

        // Trim all trailing NULs and spaces.
        // The ustar and POSIX tar specs require a trailing NUL or
        // space but some implementations use the extra digit for big
        // sizes/uids/gids ...
        byte trailer = buffer[end - 1];
        while (start < end && (trailer == 0 || trailer == ' ')) {
            end--;
            trailer = buffer[end - 1];
        }
        for (; start < end; start++) {
            final byte currentByte = buffer[start];
            if (currentByte < '0' || currentByte > '7') {
                throw new IllegalArgumentException(exceptionMessage(buffer, offset, length, start, currentByte));
            }
            result = (result << 3) + (currentByte - '0'); // convert from ASCII
        }
        return result;
    }

    /**
     * Computes the value contained in a byte buffer. If the most significant bit of the first byte in the buffer is set, this bit is ignored and the rest of
     * the buffer is interpreted as a binary number. Otherwise, the buffer is interpreted as an octal number as per the parseOctal function above.
     */
    public static long parseOctalOrBinary(byte[] buffer, int offset, int length) {
        if ((buffer[offset] & 0x80) == 0) {
            return parseOctal(buffer, offset, length);
        }
        final boolean negative = buffer[offset] == (byte) 0xff;
        if (length < 9) {
            return parseBinaryLong(buffer, offset, length, negative);
        }
        return parseBinaryBigInteger(buffer, offset, length, negative);
    }

    /**
     * For PAX Format 1.X: The sparse map itself is stored in the file data block, preceding the actual file data. It consists of a series of decimal numbers
     * delimited by newlines. The map is padded with nulls to the nearest block boundary. The first number gives the number of entries in the map. Following are
     * map entries, each one consisting of two numbers giving the offset and size of the data block it describes.
     */
    public static List<TarStructSparse> parsePAX1XSparseHeaders(InputStream inputStream, int recordSize) throws IOException {

        // for 1.X PAX Headers
        List<TarStructSparse> sparseHeaders = new ArrayList<>();
        long bytesRead = 0;
        long[] readResult = readLineOfNumberForPax1x(inputStream);
        long sparseHeadersCount = readResult[0];
        if (sparseHeadersCount < 0) {
            // overflow while reading number?
            throw new IOException("Corrupted TAR archive. Negative value in sparse headers block");
        }
        bytesRead += readResult[1];
        while (sparseHeadersCount-- > 0) {
            readResult = readLineOfNumberForPax1x(inputStream);
            final long sparseOffset = readResult[0];
            if (sparseOffset < 0) {
                throw new IOException("Corrupted TAR archive. Sparse header block offset contains negative value");
            }
            bytesRead += readResult[1];

            readResult = readLineOfNumberForPax1x(inputStream);
            final long sparseNumbytes = readResult[0];
            if (sparseNumbytes < 0) {
                throw new IOException("Corrupted TAR archive. Sparse header block numbytes contains negative value");
            }
            bytesRead += readResult[1];
            sparseHeaders.add(new TarStructSparse(sparseOffset, sparseNumbytes));
        }

        // skip the rest of this record data
        long bytesToSkip = recordSize - bytesRead % recordSize;
        skip(inputStream, bytesToSkip, () -> new byte[8192]);
        return sparseHeaders;
    }

    /**
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
     * <p>
     * For PAX Format 0.1, the sparse headers are stored in a single variable : GNU.sparse.map
     * </p>
     * <p>
     * <em>GNU.sparse.map</em>: Map of non-null data chunks. It is a string consisting of comma-separated values "offset,size[,offset-1,size-1...]"
     * </p>
     */
    public static Map<String, String> parsePaxHeaders(
            InputStream in,
            List<TarStructSparse> sparseHeaders,
            Map<String, String> globalPaxHeaders,
            long headerSize) throws IOException {

        Map<String, String> headers = new HashMap<>(globalPaxHeaders);
        Long offset = null;
        // Format is "length keyword=value\n";
        int totalRead = 0;
        while (true) { // get length
            int ch;
            int len = 0;
            int read = 0;
            while ((ch = in.read()) != -1) {
                read++;
                totalRead++;
                if (ch == '\n') { // blank line in header
                    break;
                }
                if (ch == ' ') { // End of length string
                    // Get keyword
                    ByteArrayOutputStream coll = new ByteArrayOutputStream();
                    while ((ch = in.read()) != -1) {
                        read++;
                        totalRead++;
                        if (totalRead < 0 || headerSize >= 0 && totalRead >= headerSize) {
                            break;
                        }
                        if (ch == '=') { // end of keyword
                            String keyword = coll.toString(StandardCharsets.UTF_8);
                            // Get rest of entry
                            int restLen = len - read;
                            if (restLen <= 1) { // only NL
                                headers.remove(keyword);
                            } else if (headerSize >= 0 && restLen > headerSize - totalRead) {
                                throw new IOException("Paxheader value size " + restLen + " exceeds size of header record");
                            } else {
                                byte[] rest = readRange(in, restLen);
                                int got = rest.length;
                                if (got != restLen) {
                                    throw new IOException("Failed to read Paxheader. Expected " + restLen + " bytes, read " + got);
                                }
                                totalRead += restLen;
                                // Drop trailing NL
                                if (rest[restLen - 1] != '\n') {
                                    throw new IOException("Failed to read Paxheader.Value should end with a newline");
                                }
                                String value = new String(rest, 0, restLen - 1, StandardCharsets.UTF_8);
                                headers.put(keyword, value);

                                // for 0.0 PAX Headers
                                if (keyword.equals(TarGnuSparseKeys.OFFSET)) {
                                    if (offset != null) {
                                        // previous GNU.sparse.offset header but no numBytes
                                        sparseHeaders.add(new TarStructSparse(offset, 0));
                                    }
                                    try {
                                        offset = Long.valueOf(value);
                                    } catch (NumberFormatException ex) {
                                        throw new IOException("Failed to read Paxheader." + TarGnuSparseKeys.OFFSET + " contains a non-numeric value");
                                    }
                                    if (offset < 0) {
                                        throw new IOException("Failed to read Paxheader." + TarGnuSparseKeys.OFFSET + " contains negative value");
                                    }
                                }

                                // for 0.0 PAX Headers
                                if (keyword.equals(TarGnuSparseKeys.NUMBYTES)) {
                                    if (offset == null) {
                                        throw new IOException(
                                                "Failed to read Paxheader." + TarGnuSparseKeys.OFFSET + " is expected before GNU.sparse.numbytes shows up.");
                                    }
                                    long numbytes = parseLongValue(value);
                                    if (numbytes < 0) {
                                        throw new IOException("Failed to read Paxheader." + TarGnuSparseKeys.NUMBYTES + " contains negative value");
                                    }
                                    sparseHeaders.add(new TarStructSparse(offset, numbytes));
                                    offset = null;
                                }
                            }
                            break;
                        }
                        coll.write((byte) ch);
                    }
                    break; // Processed single header
                }

                if (ch < '0' || ch > '9') {
                    throw new IOException("Failed to read Paxheader. Encountered a non-number while reading length: " + ch);
                }
                len *= 10;
                len += ch - '0';
            }
            if (ch == -1) { // EOF
                break;
            }
        }
        if (offset != null) {
            // offset but no numBytes
            sparseHeaders.add(new TarStructSparse(offset, 0));
        }
        return headers;
    }

    /**
     * Parses the content of a PAX 1.0 sparse block.
     */
    public static TarStructSparse parseSparse(byte[] buffer, final int offset) {
        long sparseOffset = parseOctalOrBinary(buffer, offset, TarConstants.SPARSE_OFFSET_LEN);
        long sparseNumbytes = parseOctalOrBinary(buffer, offset + TarConstants.SPARSE_OFFSET_LEN, TarConstants.SPARSE_NUMBYTES_LEN);
        return new TarStructSparse(sparseOffset, sparseNumbytes);
    }

    /**
     * For 1.x PAX Format, the sparse headers are stored in the file data block, preceding the actual file data. It consists of a series of decimal numbers
     * delimited by newlines.
     */
    private static long[] readLineOfNumberForPax1x(InputStream inputStream) throws IOException {
        int number;
        long result = 0;
        long bytesRead = 0;
        while ((number = inputStream.read()) != '\n') {
            bytesRead += 1;
            if (number == -1) {
                throw new IOException("Unexpected EOF when reading parse information of 1.X PAX format");
            }
            if (number < '0' || number > '9') {
                throw new IOException("Corrupted TAR archive. Non-numeric value in sparse headers block");
            }
            result = result * 10 + (number - '0');
        }
        bytesRead += 1;
        return new long[]{result, bytesRead};
    }

    public static List<TarStructSparse> readSparseStructs(byte[] buffer, final int offset, final int entries) throws IOException {
        List<TarStructSparse> sparseHeaders = new ArrayList<>();
        for (int i = 0; i < entries; i++) {
            try {
                TarStructSparse sparseHeader = parseSparse(
                        buffer,
                        offset + i * (TarConstants.SPARSE_OFFSET_LEN + TarConstants.SPARSE_NUMBYTES_LEN));
                if (sparseHeader.getOffset() < 0) {
                    throw new IOException("Corrupted TAR archive, sparse entry with negative offset");
                }
                if (sparseHeader.getNumbytes() < 0) {
                    throw new IOException("Corrupted TAR archive, sparse entry with negative numbytes");
                }
                sparseHeaders.add(sparseHeader);
            } catch (IllegalArgumentException ex) {
                // thrown internally by parseOctalOrBinary
                throw new IOException("Corrupted TAR archive, sparse entry is invalid", ex);
            }
        }
        return Collections.unmodifiableList(sparseHeaders);
    }

    public static long parseLongValue(String value) {
        try {
            return Long.parseLong(value, 10);
        } catch (NumberFormatException exp) {
            throw new RuntimeException("Unable to parse long from string value: " + value);
        }
    }

    public static int parseIntValue(String value) {
        try {
            return Integer.parseInt(value, 10);
        } catch (NumberFormatException exp) {
            throw new RuntimeException("Unable to parse int from string value: " + value);
        }
    }

    public static long skip(InputStream input, long skip, Supplier<byte[]> skipBufferSupplier) throws IOException {
        if (skip < 0L) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + skip);
        } else {
            long remain;
            long n;
            for (remain = skip; remain > 0L; remain -= n) {
                byte[] skipBuffer = skipBufferSupplier.get();
                n = input.read(skipBuffer, 0, (int) Math.min(remain, skipBuffer.length));
                if (n < 0L) {
                    break;
                }
            }

            return skip - remain;
        }
    }

    private static byte[] readRange(InputStream input, int length) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        if (length > 0L) {

            int bufferLength = Math.min(length, 8192);
            byte[] buffer = new byte[bufferLength];

            int bytesToRead = bufferLength;
            long totalRead = 0L;

            int read;
            while (bytesToRead > 0 && -1 != (read = input.read(buffer, 0, bytesToRead))) {
                output.write(buffer, 0, read);
                totalRead += read;
                bytesToRead = (int) Math.min(length - totalRead, bufferLength);
            }
        }

        return output.toByteArray();
    }

    public static boolean matchAsciiBuffer(String expected, byte[] buffer, int offset, int length) {
        final byte[] buffer1 = expected.getBytes(US_ASCII);

        int minLen = Math.min(buffer1.length, length);
        for (int i = 0; i < minLen; i++) {
            if (buffer1[i] != buffer[offset + i]) {
                return false;
            }
        }

        return buffer1.length == length;
    }

    public static int readFully(InputStream input, byte[] array, int offset, int length) throws IOException {

        Objects.requireNonNull(input);

        if (length < 0 || offset < 0 || length + offset > array.length || length + offset < 0) {
            throw new IndexOutOfBoundsException();
        }

        if (length == 0) {
            return 0;
        }

        int remaining;
        int count;
        for (remaining = length; remaining > 0; remaining -= count) {
            int location = length - remaining;
            count = input.read(array, offset + location, remaining);
            if (-1 == count) {
                break;
            }
        }

        return length - remaining;
    }

    public static boolean isArrayZero(byte[] a, final int size) {
        for (int i = 0; i < size; i++) {
            if (a[i] != 0) {
                return false;
            }
        }
        return true;
    }

    private TarUtils() {
    }
}
