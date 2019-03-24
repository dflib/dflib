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
    protected BasePrinterWorker newWorker(StringBuilder out) {
        return new InlinePrinterWorker(out, maxDisplayRows, maxDisplayColumnWith);
    }
}
