package com.nhl.dflib.print;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;

public abstract class BasePrinter implements Printer {

    private static final int MAX_DISPLAY_ROWS = 3;
    private static final int MAX_DISPLAY_COLUMN_WIDTH = 30;

    protected int maxDisplayRows;
    protected int maxDisplayColumnWidth;

    protected BasePrinter() {
        this(MAX_DISPLAY_ROWS, MAX_DISPLAY_COLUMN_WIDTH);
    }

    protected BasePrinter(int maxDisplayRows, int maxDisplayColumnWidth) {
        this.maxDisplayRows = maxDisplayRows;
        this.maxDisplayColumnWidth = maxDisplayColumnWidth;
    }

    @Override
    public StringBuilder print(StringBuilder out, DataFrame df) {
        return newDataFrameWorker(out).print(df);
    }

    @Override
    public StringBuilder print(StringBuilder out, Series<?> s) {
        return newSeriesWorker(out).print(s);
    }

    protected abstract SeriesPrintWorker newSeriesWorker(StringBuilder out);

    protected abstract DataFramePrintWorker newDataFrameWorker(StringBuilder out);
}
