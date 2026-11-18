package org.dflib.excel;

/**
 * Workbook data either from a POI usermodel workbook or from the streaming XLSX reader.
 */
interface WorkbookData extends AutoCloseable {

    int sheetCount();

    String sheetName(int index);

    /**
     * Returns a position of the sheet with the name (case-insensitive, as in Excel), or -1 if there's no such sheet.
     */
    int sheetIndex(String name);

    /**
     * Reads the sheet at the position. "rowOffset" and "rowLimit" (counted from the first non-phantom row) are hints
     * that let the reader skip the rows that will not be loaded. They must not affect {@link SheetData#valuesRange()}.
     */
    SheetData sheet(int index, int rowOffset, int rowLimit);

    @Override
    void close();
}
