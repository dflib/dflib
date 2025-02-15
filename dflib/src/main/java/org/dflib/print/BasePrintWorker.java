package org.dflib.print;

import java.io.IOException;

public abstract class BasePrintWorker {

    protected Appendable out;
    protected int maxDisplayColumnWidth;
    protected int maxDisplayRows;

    public BasePrintWorker(Appendable out, int maxDisplayRows, int maxDisplayColumnWidth) {
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

    protected String columnFormat(int width, Class<?> valueType) {
        if (width <= 0) {
            throw new IllegalArgumentException("Column width must be positive: " + width);
        }

        return valueType.isPrimitive()
                || Number.class.isAssignableFrom(valueType)
                || Boolean.class.isAssignableFrom(valueType)
                // numbers are right-aligned
                ? "%1$" + width + "s"
                // the rest are left-aligned
                : "%1$-" + width + "s";
    }

    protected void appendFixedWidth(String value, int width, String columnFormat) throws IOException {

        if (value == null || value.length() <= width) {
            out.append(String.format(columnFormat, value));
        } else {
            out.append(truncate(value, width));
        }
    }

    protected void appendNewLine() throws IOException {
        out.append(System.lineSeparator());
    }

    protected void appendTruncate(String value) throws IOException {
        out.append(truncate(value, maxDisplayColumnWidth));
    }
}
