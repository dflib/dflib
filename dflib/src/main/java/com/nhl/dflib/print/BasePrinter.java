package com.nhl.dflib.print;

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
}
