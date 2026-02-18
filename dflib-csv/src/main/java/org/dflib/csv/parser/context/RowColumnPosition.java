package org.dflib.csv.parser.context;

/**
 * Simple row/column information.
 * Internal API. Part of the {@link org.dflib.csv.CsvLoader} API
 *
 * @since 2.0.0
 */
public class RowColumnPosition {

    public int row;
    public int column;

    public RowColumnPosition() {
        this.row = 0;
        this.column = 0;
    }

    public void advanceColumn() {
        column++;
    }

    public void advanceRow() {
        row++;
        column = 0;
    }

    public void resetColumn() {
        column = 0;
    }

    @Override
    public String toString() {
        return "{row=" + row + ", column=" + column + '}';
    }
}
