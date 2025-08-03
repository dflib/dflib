package org.dflib.tar.format;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @since 2.0.0
 */
class BoundedInputStream extends FilterInputStream {

    private final long maxCount;
    private long count;
    private long mark;

    BoundedInputStream(InputStream delegate, long maxCount) {
        super(delegate);
        this.count = 0;
        this.maxCount = maxCount;
    }

    @Override
    public int available() throws IOException {
        if (this.isMaxCount()) {
            return 0;
        } else {
            return in.available();
        }
    }

    private boolean isMaxCount() {
        return maxCount >= 0L && count >= maxCount;
    }

    @Override
    public synchronized void mark(int readLimit) {
        this.in.mark(readLimit);
        this.mark = this.count;
    }

    @Override
    public boolean markSupported() {
        return this.in.markSupported();
    }

    @Override
    public int read() throws IOException {
        if (this.isMaxCount()) {
            return -1;
        } else {
            return super.read();
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.isMaxCount()) {
            return -1;
        } else {
            return super.read(b, off, (int) this.toReadLen((long) len));
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        this.in.reset();
        this.count = this.mark;
    }

    @Override
    public synchronized long skip(long n) throws IOException {
        long skip = super.skip(this.toReadLen(n));
        this.count += skip;
        return skip;
    }

    private long toReadLen(long len) {
        return maxCount >= 0L ? Math.min(len, maxCount - count) : len;
    }
}

