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
    protected BasePrinterWorker newWorker(StringBuilder out) {
        return new TabularPrinterWorker(out, maxDisplayRows, maxDisplayColumnWith);
    }
}
