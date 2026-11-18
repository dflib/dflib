package org.dflib.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * A {@link SheetData} over a POI usermodel {@link Sheet}.
 */
class PoiSheetData implements SheetData {

    private final Sheet sheet;

    PoiSheetData(Sheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public SheetRange valuesRange() {
        SheetRange.Builder builder = new SheetRange.Builder();
        for (Row r : sheet) {
            builder.addRow(r.getRowNum(), r.getFirstCellNum(), r.getLastCellNum());
        }

        return builder.build();
    }

    @Override
    public RowData row(int rowNum) {
        Row row = sheet.getRow(rowNum);
        return row != null ? new PoiRowData(row) : null;
    }

    static class PoiRowData implements RowData {

        private final Row row;

        PoiRowData(Row row) {
            this.row = row;
        }

        @Override
        public Object value(int col) {
            Cell cell = row.getCell(col, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            return cell != null ? value(cell) : null;
        }

        private static Object value(Cell cell) {

            CellType type = cell.getCellType() == CellType.FORMULA
                    ? cell.getCachedFormulaResultType() : cell.getCellType();

            return switch (type) {
                case STRING -> cell.getStringCellValue();
                // TODO: check the actual format to return LocalDate, LocalDateTime or smth else
                case NUMERIC -> DateUtil.isCellDateFormatted(cell)
                        ? cell.getLocalDateTimeCellValue()
                        : cell.getNumericCellValue();
                case BOOLEAN -> cell.getBooleanCellValue();
                case FORMULA -> throw new IllegalStateException("FORMULA is unexpected as a FORMULA cell result");
                default -> null;
            };
        }
    }
}
