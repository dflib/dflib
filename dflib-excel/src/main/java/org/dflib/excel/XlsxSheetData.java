package org.dflib.excel;

/**
 * Sheet data read by the streaming XLSX reader. Only stores the rows with values within the requested row window,
 * while the range is calculated over all sheet rows.
 */
class XlsxSheetData implements SheetData {

    private final SheetRange range;

    // ordered by row number
    private final XlsxRowData[] rows;

    XlsxSheetData(SheetRange range, XlsxRowData[] rows) {
        this.range = range;
        this.rows = rows;
    }

    @Override
    public SheetRange valuesRange() {
        return range;
    }

    @Override
    public RowData row(int rowNum) {

        int low = 0;
        int high = rows.length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            int midRowNum = rows[mid].rowNum;

            if (midRowNum < rowNum) {
                low = mid + 1;
            } else if (midRowNum > rowNum) {
                high = mid - 1;
            } else {
                return rows[mid];
            }
        }

        return null;
    }

    static class XlsxRowData implements RowData {

        final int rowNum;
        private final int valuesOffset;
        private final Object[] values;

        XlsxRowData(int rowNum, int valuesOffset, Object[] values) {
            this.rowNum = rowNum;
            this.valuesOffset = valuesOffset;
            this.values = values;
        }

        @Override
        public Object value(int col) {
            int i = col - valuesOffset;
            return i >= 0 && i < values.length ? values[i] : null;
        }
    }
}
