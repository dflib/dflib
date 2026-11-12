package org.dflib.excel;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;

/**
 * A {@link WorkbookData} over a POI usermodel {@link Workbook}. Used for the legacy XLS format and other files that
 * can't be read by the streaming {@link XlsxWorkbookData}.
 */
class PoiWorkbookData implements WorkbookData {

    private final Workbook workbook;

    PoiWorkbookData(Workbook workbook) {
        this.workbook = workbook;
    }

    @Override
    public int sheetCount() {
        return workbook.getNumberOfSheets();
    }

    @Override
    public String sheetName(int index) {
        return workbook.getSheetName(index);
    }

    @Override
    public int sheetIndex(String name) {
        return workbook.getSheetIndex(name);
    }

    @Override
    public SheetData sheet(int index, int rowOffset, int rowLimit) {
        // the whole workbook is already in memory, so ignoring the offset and limit hints
        return new PoiSheetData(workbook.getSheetAt(index));
    }

    @Override
    public void close() {
        try {
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing Excel workbook", e);
        }
    }
}
