package org.dflib.json;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;

class AppendableWriter extends Writer {

    private final Appendable appendable;

    static Writer asWriter(Appendable appendable) {
        return appendable instanceof Writer ? (Writer) appendable : new AppendableWriter(appendable);
    }

    AppendableWriter(Appendable appendable) {
        this.appendable = appendable;
    }

    @Override
    public void write(int c) throws IOException {
        appendable.append((char) c);
    }

    @Override
    public void write(String str, int off, int len) throws IOException {
        appendable.append(str, off, off + len);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        appendable.append(new String(cbuf, off, len));
    }

    @Override
    public void flush() throws IOException {
        if (appendable instanceof Flushable) {
            ((Flushable) appendable).flush();
        }
    }

    @Override
    public void close() throws IOException {
        if (appendable instanceof Closeable) {
            ((Closeable) appendable).close();
        }
    }
}
