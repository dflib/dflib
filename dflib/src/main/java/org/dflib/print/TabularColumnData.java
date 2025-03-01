package org.dflib.print;

import java.io.IOException;

class TabularColumnData {

    private final String[] data;
    private final CellFormatter formatter;
    private final int width;

    public static Builder builder(Class<?> type, int height, int maxValChars) {
        return new Builder(type, height, maxValChars);
    }

    private TabularColumnData(String[] data, CellFormatter formatter, int width) {
        this.data = data;
        this.formatter = formatter;
        this.width = width;
    }

    public void printSeparatorTo(Appendable out) throws IOException {
        for (int i = 0; i < width; i++) {
            out.append("-");
        }
    }

    public void printTo(Appendable out, int pos) throws IOException {
        String val = data[pos];
        if (val == null || val.length() <= width) {
            out.append(formatter.format(val));
        } else {
            out.append(truncate(val));
        }
    }

    private String truncate(String string) {

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

    static class Builder {

        private final Class<?> type;
        private final int maxValChars;
        private final String[] vals;
        private int pos;

        private Builder(Class<?> type, int height, int maxValChars) {
            this.type = type;
            this.vals = new String[height];
            this.maxValChars = maxValChars;
        }

        Builder append(Object val) {
            vals[pos++] = String.valueOf(val);
            return this;
        }

        TabularColumnData build() {
            int maxW = buildWidth();
            CellFormatter formatter = buildFormatter(maxW);
            return new TabularColumnData(vals, formatter, maxW);
        }

        private int buildWidth() {
            int maxW = 1;

            int h = vals.length;
            for (int i = 0; i < h; i++) {
                if (vals[i] != null) {
                    maxW = Math.max(maxW, vals[i].length());
                }
            }

            return Math.max(1, Math.min(maxW, maxValChars));
        }

        private CellFormatter buildFormatter(int width) {
            return type.isPrimitive()
                    || Number.class.isAssignableFrom(type)
                    || Boolean.class.isAssignableFrom(type)

                    // numbers are right-aligned, the rest are left-aligned
                    ? org.dflib.print.CellFormatter.leftPad(width) : org.dflib.print.CellFormatter.rightPad(width);
        }
    }
}
