package com.nhl.dflib.print;

import com.nhl.dflib.DataFrame;

public abstract class DataFramePrintWorker {

    protected StringBuilder out;
    protected int maxDisplayColumnWidth;
    protected int maxDisplayRows;

    public DataFramePrintWorker(StringBuilder out, int maxDisplayRows, int maxDisplayColumnWidth) {
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

    abstract StringBuilder print(DataFrame df);
}
