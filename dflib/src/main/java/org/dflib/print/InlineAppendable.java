package org.dflib.print;

import java.io.IOException;

abstract class InlineAppendable {

    protected final Appendable out;
    protected final int maxDisplayColumnWidth;
    protected final int maxDisplayRows;

    public InlineAppendable(Appendable out, int maxDisplayRows, int maxDisplayColumnWidth) {
        this.out = out;
        this.maxDisplayColumnWidth = maxDisplayColumnWidth;
        this.maxDisplayRows = maxDisplayRows;
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

    protected void appendTruncate(String value) throws IOException {
        out.append(truncate(value, maxDisplayColumnWidth));
    }
}
