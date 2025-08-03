package org.dflib.tar.format;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * NIO backed bounded input stream for reading a predefined amount of data. This base class is thread-safe but
 * implementations must not be.
 *
 * @since 2.0.0
 */
abstract class BoundedArchiveInputStream extends InputStream {

    private final long end;
    private ByteBuffer singleByteBuffer;
    private long loc;

    public BoundedArchiveInputStream(long start, long remaining) {
        this.end = start + remaining;
        if (this.end < start) {
            // check for potential vulnerability due to overflow
            throw new IllegalArgumentException("Invalid length of stream at offset=" + start + ", length=" + remaining);
        }
        loc = start;
    }

    @Override
    public synchronized int read() throws IOException {
        if (loc >= end) {
            return -1;
        }
        if (singleByteBuffer == null) {
            singleByteBuffer = ByteBuffer.allocate(1);
        } else {
            singleByteBuffer.rewind();
        }
        int read = read(loc, singleByteBuffer);
        if (read < 1) {
            return -1;
        }
        loc++;
        return singleByteBuffer.get() & 0xff;
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        if (loc >= end) {
            return -1;
        }
        final long maxLen = Math.min(len, end - loc);
        if (maxLen <= 0) {
            return 0;
        }
        if (off < 0 || off > b.length || maxLen > b.length - off) {
            throw new IndexOutOfBoundsException("offset or len are out of bounds");
        }

        final ByteBuffer buf = ByteBuffer.wrap(b, off, (int) maxLen);
        final int ret = read(loc, buf);
        if (ret > 0) {
            loc += ret;
        }
        return ret;
    }

    /**
     * Reads content of the stream into a {@link ByteBuffer}.
     */
    protected abstract int read(long pos, ByteBuffer buf) throws IOException;
}
