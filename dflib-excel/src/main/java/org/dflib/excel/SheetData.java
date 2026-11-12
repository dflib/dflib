package org.dflib.excel;

/**
 * Sheet data either from a POI usermodel sheet or from the streaming XLSX reader.
 */
interface SheetData {

    /**
     * Returns a range of the sheet values, excluding leading and trailing "phantom" rows.
     */
    SheetRange valuesRange();

    /**
     * Returns a row for the 0-based row number, or null if the row is missing or was not retained by the reader.
     */
    RowData row(int rowNum);
}
