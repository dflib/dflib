package com.nhl.dflib.excel;

import com.nhl.dflib.Index;
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


        for (Row r : sh) {

            // skip "phantom" rows from range calculation. The effect of this is stripping leading and
            // trailing phantom rows, but keeping those in the middle of the values
            if (isPhantom(r)) {
                continue;
            }

            startRow = Math.min(r.getRowNum(), startRow);
            endRow = Math.max(r.getRowNum(), endRow);

            startCol = Math.min(r.getFirstCellNum(), startCol);
            endCol = Math.max(r.getLastCellNum(), endCol);
        }


        return new SheetRange(
                startCol,
                // "endCol" already points past the last column
                endCol,
                startRow,
                // as "endRow" is an index, we must increment it to point past the last row
                endRow + 1);
    }

    private static boolean isPhantom(Row row) {
        return row.getFirstCellNum() < 0;
    }

    public boolean isEmpty() {
        return height <= 0;
    }

    Index columns() {
        String[] names = new String[width];
        for (int i = 0; i < width; i++) {
            names[i] = CellReference.convertNumToColString(startCol + i);
        }

        return Index.forLabels(names);
    }
}
