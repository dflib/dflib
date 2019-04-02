package com.nhl.dflib.print;

/**
 * A utility class for outputting DataFrames and DataRows on a single line.
 */
public class InlinePrinter extends BasePrinter {

    public InlinePrinter() {
    }

    public InlinePrinter(int maxDisplayRows, int maxDisplayColumnWith) {
        super(maxDisplayRows, maxDisplayColumnWith);
    }

    @Override
    protected DataFramePrintWorker newDataFrameWorker(StringBuilder out) {
        return new DataFrameInlinePrintWorker(out, maxDisplayRows, maxDisplayColumnWidth);
    }

    @Override
    protected SeriesPrintWorker newSeriesWorker(StringBuilder out) {
        return new SeriesInlinePrintWorker(out, maxDisplayRows, maxDisplayColumnWidth);
    }
}
