package org.dflib.excel;

/**
 * Row data either from a POI usermodel row or from the streaming XLSX reader.
 */
interface RowData {

    /**
     * Returns a value of the cell at the specified position or null if the cell is missing or blank.
     */
    Object value(int col);
}
