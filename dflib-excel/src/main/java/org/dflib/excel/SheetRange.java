package org.dflib.excel;

import org.dflib.Index;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
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

    static SheetRange valuesRange(Sheet sh) {

        int startRow = Integer.MAX_VALUE;
        int endRow = Integer.MIN_VALUE;

        int startCol = Short.MAX_VALUE + 1;
        int endCol = Short.MIN_VALUE - 1;

        boolean allPhantom = true;

        for (Row r : sh) {

            // skip "phantom" rows from range calculation. The effect of this is stripping leading and
            // trailing phantom rows, but keeping those in the middle of the values
            if (!isPhantom(r)) {

                allPhantom = false;

                startRow = Math.min(r.getRowNum(), startRow);
                endRow = Math.max(r.getRowNum(), endRow);

                startCol = Math.min(r.getFirstCellNum(), startCol);
                endCol = Math.max(r.getLastCellNum(), endCol);
            }
        }

        return allPhantom
                ? new SheetRange(0, 0, 0, 0)

                // "endCol" already points past the last column
                // as "endRow" is an index, we must increment it to point past the last row
                : new SheetRange(startCol, endCol, startRow, endRow + 1);
    }

    private static boolean isPhantom(Row row) {
        return row.getFirstCellNum() < 0;
    }

    SheetRange skipRows(int rows) {
        if (rows <= 0) {
            return this;
        }

        return rows >= height
                ? new SheetRange(startCol, endCol, endRow, endRow)
                : new SheetRange(startCol, endCol, startRow + rows, endRow);
    }

    Index columns() {
        String[] names = new String[width];

        for (int i = 0; i < width; i++) {
            names[i] = CellReference.convertNumToColString(startCol + i);
        }

        return Index.of(names);
    }
}
