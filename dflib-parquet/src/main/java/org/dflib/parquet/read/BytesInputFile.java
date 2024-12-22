package org.dflib.parquet.read;

import org.apache.parquet.io.InputFile;
import org.apache.parquet.io.SeekableInputStream;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @since 1.1.0
 */
public class BytesInputFile implements InputFile {

    private final byte[] data;

    public BytesInputFile(byte[] data) {
        this.data = data;
    }

    @Override
    public long getLength() {
        return data.length;
    }

    @Override
    public SeekableInputStream newStream() {

        return new SeekableInputStream() {

            private final ByteArrayInputStream in = new ByteArrayInputStream(data);

            @Override
            public long getPos() {
                return data.length - in.available();
            }

            @Override
            public void seek(long newPos) {
                in.reset();
                in.skip(newPos);
            }

            @Override
            public int read() {
                return in.read();
            }

            @Override
            public int read(ByteBuffer buf) throws IOException {
                byte[] buffer = new byte[buf.remaining()];
                int code = read(buffer);
                buf.put(buffer, buf.position() + buf.arrayOffset(), buf.remaining());
                return code;
            }

            @Override
            public void readFully(byte[] bytes) throws IOException {
                readFully(bytes, 0, bytes.length);
            }

            @Override
            public void readFully(byte[] bytes, int start, int len) throws IOException {
                int read = 0;
                do {
                    int count = in.read(bytes, read, len - read);
                    if (count < 0) {
                        throw new EOFException();
                    }
                    read += count;
                } while (read < len);
            }

            @Override
            public void readFully(ByteBuffer buf) throws IOException {
                byte[] buffer = new byte[buf.remaining()];
                readFully(buffer);
                buf.put(buffer, buf.position() + buf.arrayOffset(), buf.remaining());
            }
        };
    }
}
