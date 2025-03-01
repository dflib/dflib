package org.dflib.print;

import java.io.IOException;

abstract class InlineAppendable {

    protected final Appendable out;
    protected final int maxColumnChars;
    protected final int maxRows;

    public InlineAppendable(Appendable out, int maxRows, int maxColumnChars) {
        this.out = out;
        this.maxColumnChars = maxColumnChars;
        this.maxRows = maxRows;
    }

    protected static String truncate(String string, int width) {

        int len = string.length();
        if (len <= width) {
            return string;
        }

        if (width <= 2) {
            return "..";
        }

        int offset = width / 2 - 1;
        int startOffset = offset + width % 2;
        int endOffset = len - offset;

        return string.substring(0, startOffset) + ".." + string.substring(endOffset);
    }

    protected void printTruncate(String value) throws IOException {
        out.append(truncate(value, maxColumnChars));
    }
}
