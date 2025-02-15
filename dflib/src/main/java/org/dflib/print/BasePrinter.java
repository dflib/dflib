package org.dflib.print;

public abstract class BasePrinter implements Printer {

    private static final int MAX_DISPLAY_ROWS = 5;
    private static final int MAX_DISPLAY_COLUMN_WIDTH = 50;

    protected int maxDisplayRows;
    protected int maxDisplayColumnWidth;

    protected BasePrinter() {
        this(MAX_DISPLAY_ROWS, MAX_DISPLAY_COLUMN_WIDTH);
    }

    protected BasePrinter(int maxDisplayRows, int maxDisplayColumnWidth) {
        this.maxDisplayRows = maxDisplayRows;
        this.maxDisplayColumnWidth = maxDisplayColumnWidth;
    }
}
