package org.dflib.print;

public abstract class BasePrintWorker {

    protected StringBuilder out;
    protected int maxDisplayColumnWidth;
    protected int maxDisplayRows;

    public BasePrintWorker(StringBuilder out, int maxDisplayRows, int maxDisplayColumnWidth) {
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

    protected StringBuilder appendFixedWidth(String value, int width, String columnFormat) {

        if (value == null || value.length() <= width) {
            return out.append(String.format(columnFormat, value));
        } else {
            return out.append(truncate(value, width));
        }
    }

    protected StringBuilder appendNewLine() {
        return out.append(System.lineSeparator());
    }

    protected StringBuilder appendTruncate(String value) {
        return out.append(truncate(value, maxDisplayColumnWidth));
    }
}
