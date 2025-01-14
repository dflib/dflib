package org.dflib.csv;

import java.io.IOException;
import java.io.InputStream;

class PostBOMInputStream extends InputStream {

    private final InputStream delegate;

    private byte[] head;
    private int headLen;
    private int headOffset;

    public PostBOMInputStream(InputStream delegate, byte[] head, int headLen) {
        this.delegate = delegate;
        this.head = head;
        this.headLen = headLen;
        this.headOffset = 0;
    }

    @Override
    public int read() throws IOException {
        return head != null ? readHead() : delegate.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return head != null ? readHead(b, off, len) : delegate.read(b, off, len);
    }

    private int readHead() {
        int v = head[headOffset++];

        if (headOffset >= headLen) {
            head = null;
            headLen = 0;
        }

        return v;
    }

    private int readHead(byte[] b, int off, int len) throws IOException {

        int delta = headLen - headOffset;

        int readFromHead = Math.min(delta, len);
        System.arraycopy(head, headOffset, b, off, readFromHead);

        headOffset += readFromHead;
        if (headOffset >= headLen) {
            head = null;
            headLen = 0;
        }

        if(delta >= len) {
            return readFromHead;
        }

        int readFromStream = delegate.read(b, off + delta, len - delta);
        return readFromStream == -1 ? readFromHead : readFromHead + readFromStream;
    }
}
