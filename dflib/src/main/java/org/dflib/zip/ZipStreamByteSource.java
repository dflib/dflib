package org.dflib.zip;

import org.dflib.ByteSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @since 2.0.0
 */
class ZipStreamByteSource implements ByteSource {

    private final InputStream in;
    private final AtomicBoolean streamUsed;

    public ZipStreamByteSource(InputStream in) {
        this.in = in;
        this.streamUsed = new AtomicBoolean(false);
    }

    @Override
    public InputStream stream() {

        // TODO: should we reset the stream instead?
        if (streamUsed.compareAndExchange(false, true)) {
            throw new IllegalStateException("Stream was already consumed. " +
                    "This ByteSource works over an already open stream and can not be reused");
        }

        return new UnclosableStream(in);
    }

    static class UnclosableStream extends InputStream {

        private final InputStream delegate;

        public UnclosableStream(InputStream delegate) {
            this.delegate = delegate;
        }

        @Override
        public void close() throws IOException {
            // do nothing. Stream closing is managed externally
        }

        @Override
        public int read() throws IOException {
            return delegate.read();
        }

        @Override
        public int read(byte[] b) throws IOException {
            return delegate.read(b);
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return delegate.read(b, off, len);
        }

        @Override
        public byte[] readAllBytes() throws IOException {
            return delegate.readAllBytes();
        }

        @Override
        public byte[] readNBytes(int len) throws IOException {
            return delegate.readNBytes(len);
        }

        @Override
        public int readNBytes(byte[] b, int off, int len) throws IOException {
            return delegate.readNBytes(b, off, len);
        }
    }
}
