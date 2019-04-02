package com.nhl.dflib.print;

import com.nhl.dflib.DataFrame;

public abstract class BasePrinter implements Printer {

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

    @Override
    public StringBuilder print(StringBuilder out, DataFrame df) {
        return newWorker(out).print(df);
    }

    protected abstract BasePrinterWorker newWorker(StringBuilder out);
}
