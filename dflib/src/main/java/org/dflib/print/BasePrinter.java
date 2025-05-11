package org.dflib.print;

public abstract class BasePrinter implements Printer {

    private static final int MAX_ROWS = 10;
    private static final int MAX_COLS = 100;
    private static final int MAX_VALUE_CHARS = 50;

    protected int maxRows;
    protected int maxCols;
    protected int maxValueChars;

    protected BasePrinter() {
        this(MAX_ROWS, MAX_COLS, MAX_VALUE_CHARS);
    }

    protected BasePrinter(int maxRows, int maxCols, int maxValueChars) {
        this.maxRows = maxRows;
        this.maxCols = maxCols;
        this.maxValueChars = maxValueChars;
    }
}
