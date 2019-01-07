package com.nhl.yadf.print;

/**
 * A utility class for outputting DataFrames and DataRows as constrained tables.
 */
public class TabularPrinter extends BasePrinter {

    private static final TabularPrinter DEFAULT_PRINTER = new TabularPrinter();

    public TabularPrinter() {
    }

    public TabularPrinter(int maxDisplayRows, int maxDisplayColumnWith) {
        super(maxDisplayRows, maxDisplayColumnWith);
    }

    public static TabularPrinter getInstance() {
        return DEFAULT_PRINTER;
    }

    @Override
    protected BasePrinterWorker newWorker(StringBuilder out) {
        return new TabularPrinterWorker(out, maxDisplayRows, maxDisplayColumnWith);
    }
}
