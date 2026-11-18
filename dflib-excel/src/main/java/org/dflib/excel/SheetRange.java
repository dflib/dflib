package org.dflib.excel;

import org.dflib.Index;
import org.apache.poi.ss.util.CellReference;

class SheetRange {

    final int startCol;
    final int endCol;
    final int startRow;
    final int endRow;
    final int width;
    final int height;

    private SheetRange(int startCol, int endCol, int startRow, int endRow) {
        this.startCol = startCol;
        this.endCol = endCol;
        this.startRow = startRow;
        this.endRow = endRow;
        this.width = endCol < 0 ? -1 : endCol - startCol;
        this.height = endRow < 0 ? -1 : endRow - startRow;
    }

    SheetRange offset(int rows) {
        if (rows <= 0) {
            return this;
        }

        return rows >= height
                ? new SheetRange(startCol, endCol, endRow, endRow)
                : new SheetRange(startCol, endCol, startRow + rows, endRow);
    }

    SheetRange limit(int rows) {
        if (rows < 0) {
            return this;
        }

        return rows >= height
                ? this
                : new SheetRange(startCol, endCol, startRow, startRow + rows);
    }

    Index header() {
        String[] names = new String[width];

        for (int i = 0; i < width; i++) {
            names[i] = CellReference.convertNumToColString(startCol + i);
        }

        return Index.of(names);
    }

    Index header(RowData row) {

        Index defaultHeader = header();
        String[] labels = new String[width];

        for (int i = 0; i < width; i++) {
            int pos = startCol + i;
            Object val = ColConfigurator.value(row, pos);
            labels[i] = val != null ? val.toString() : defaultHeader.get(i);
        }

        return Index.ofDeduplicated(labels);
    }

    /**
     * Calculates a range of values from the sheet rows passed to it one by one.
     */
    static class Builder {

        private int startRow;
        private int endRow;
        private int startCol;
        private int endCol;
        private boolean allPhantom;

        Builder() {
            this.startRow = Integer.MAX_VALUE;
            this.endRow = Integer.MIN_VALUE;
            this.startCol = Short.MAX_VALUE + 1;
            this.endCol = Short.MIN_VALUE - 1;
            this.allPhantom = true;
        }

        /**
         * Adds a row to the range calculation.
         *
         * @param rowNum   0-based row number
         * @param firstCol position of the first physical cell or -1 if the row has no cells
         * @param lastCol  position past the last physical cell or -1 if the row has no cells
         */
        Builder addRow(int rowNum, int firstCol, int lastCol) {

            // skip "phantom" rows from range calculation. The effect of this is stripping leading and
            // trailing phantom rows, but keeping those in the middle of the values
            if (firstCol >= 0) {

                allPhantom = false;

                startRow = Math.min(rowNum, startRow);
                endRow = Math.max(rowNum, endRow);

                startCol = Math.min(firstCol, startCol);
                endCol = Math.max(lastCol, endCol);
            }

            return this;
        }

        SheetRange build() {
            return allPhantom
                    ? new SheetRange(0, 0, 0, 0)

                    // "endCol" already points past the last column
                    // as "endRow" is an index, we must increment it to point past the last row
                    : new SheetRange(startCol, endCol, startRow, endRow + 1);
        }
    }
}
