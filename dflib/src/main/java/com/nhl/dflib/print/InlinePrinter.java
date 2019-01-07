package com.nhl.dflib.print;

/**
 * A utility class for outputting DataFrames and DataRows on a single line.
 */
public class InlinePrinter extends BasePrinter {

    private static final InlinePrinter DEFAULT_PRINTER = new InlinePrinter();

    public InlinePrinter() {
    }

    public InlinePrinter(int maxDisplayRows, int maxDisplayColumnWith) {
        super(maxDisplayRows, maxDisplayColumnWith);
    }

    public static InlinePrinter getInstance() {
        return DEFAULT_PRINTER;
    }

    @Override
    protected BasePrinterWorker newWorker(StringBuilder out) {
        return new InlinePrinterWorker(out, maxDisplayRows, maxDisplayColumnWith);
    }
}
