package com.nhl.yadf.print;

import com.nhl.yadf.Index;

import java.util.Iterator;

public abstract class BasePrinterWorker {

    protected StringBuilder out;
    protected int maxDisplayColumnWith;
    protected int maxDisplayRows;

    BasePrinterWorker(StringBuilder out, int maxDisplayRows, int maxDisplayColumnWith) {
        this.out = out;
        this.maxDisplayColumnWith = maxDisplayColumnWith;
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

    abstract StringBuilder print(Index index, Iterator<Object[]> values);
}
