package com.nhl.yadf.print;

import com.nhl.yadf.DataFrame;

public abstract class BasePrinter {

    private static final int MAX_DISPLAY_ROWS = 3;
    private static final int MAX_DISPLAY_COLUMN_WIDTH = 30;

    protected int maxDisplayRows;
    protected int maxDisplayColumnWith;

    protected BasePrinter() {
        this(MAX_DISPLAY_ROWS, MAX_DISPLAY_COLUMN_WIDTH);
    }

    protected BasePrinter(int maxDisplayRows, int maxDisplayColumnWith) {
        this.maxDisplayRows = maxDisplayRows;
        this.maxDisplayColumnWith = maxDisplayColumnWith;
    }

    public String toString(DataFrame df) {
        return print(new StringBuilder(), df).toString();
    }

    public StringBuilder print(StringBuilder out, DataFrame df) {
        return newWorker(out).print(df.getColumns(), df.iterator());
    }

    protected abstract BasePrinterWorker newWorker(StringBuilder out);
}
