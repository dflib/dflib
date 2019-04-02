package com.nhl.dflib.print;

/**
 * A utility class for outputting DataFrames and DataRows as constrained tables.
 */
public class TabularPrinter extends BasePrinter {

    public TabularPrinter() {
    }

    public TabularPrinter(int maxDisplayRows, int maxDisplayColumnWith) {
        super(maxDisplayRows, maxDisplayColumnWith);
    }

    @Override
    protected DataFramePrintWorker newDataFrameWorker(StringBuilder out) {
        return new DataFrameTabularPrintWorker(out, maxDisplayRows, maxDisplayColumnWidth);
    }

    @Override
    protected SeriesPrintWorker newSeriesWorker(StringBuilder out) {
        return new SeriesTabularPrintWorker(out, maxDisplayRows, maxDisplayColumnWidth);
    }
}
