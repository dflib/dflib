package org.dflib.excel;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

class ExcelSaverDataUpdater {

    private final DataFrame dataFrame;

    private final Sheet sheet;
    private final int sheetRowOffset;
    private final Map<String, CellStyle> stylesByColumn;
    private final Map<Class, CellStyle> stylesByType;

    public ExcelSaverDataUpdater(DataFrame dataFrame, Sheet sheet, int sheetRowOffset, Map<String, CellStyle> stylesByColumn) {
        this.dataFrame = dataFrame;
        this.sheet = sheet;
        this.sheetRowOffset = sheetRowOffset;
        this.stylesByColumn = stylesByColumn;
        this.stylesByType = new HashMap<>();
    }

    void update() {
        int w = dataFrame.width();
        for (int i = 0; i < w; i++) {
            updateColumn(i);
        }
    }

    private void updateColumn(int columnPos) {

        Series<?> column = dataFrame.getColumn(columnPos);
        Class<?> type = column.getNominalType();

        if (Boolean.TYPE.equals(type)) {
            updateTypedColumn(columnPos, Boolean.class, Cell::setCellValue);
        } else if (Integer.TYPE.equals(type)) {
            updateTypedColumn(columnPos, Integer.class, Cell::setCellValue);
        } else if (Long.TYPE.equals(type)) {
            updateTypedColumn(columnPos, Long.class, Cell::setCellValue);
        } else if (Double.TYPE.equals(type)) {
            updateTypedColumn(columnPos, Double.class, Cell::setCellValue);
        } else {
            updateAnyTypeColumn(columnPos);
        }
    }

    private <T> void updateTypedColumn(int columnPos, Class<T> type, BiConsumer<Cell, T> cellUpdater) {

        Series<T> column = dataFrame.getColumn(columnPos);
        String columName = dataFrame.getColumnsIndex().get(columnPos);

        // since all the values are of the same type, we can precalculate the style
        CellStyle style = cellStyle(columName, type);

        int h = dataFrame.height();
        for (int i = 0; i < h; i++) {
            Row existingRow = sheet.getRow(sheetRowOffset + i);
            Row row = existingRow != null ? existingRow : sheet.createRow(sheetRowOffset + i);
            Cell cell = row.createCell(columnPos);

            cellUpdater.accept(cell, column.get(i));

            if (style != null) {
                cell.setCellStyle(style);
            }
        }
    }

    private void updateAnyTypeColumn(int columnPos) {

        Series<?> column = dataFrame.getColumn(columnPos);
        String columName = dataFrame.getColumnsIndex().get(columnPos);

        int h = dataFrame.height();
        for (int i = 0; i < h; i++) {
            Row existingRow = sheet.getRow(sheetRowOffset + i);
            Row row = existingRow != null ? existingRow : sheet.createRow(sheetRowOffset + i);
            Cell cell = row.createCell(columnPos);

            Object val = column.get(i);
            setValue(cell, val);

            CellStyle style = cellStyle(columName, val != null ? val.getClass() : null);
            if (style != null) {
                cell.setCellStyle(style);
            }
        }
    }

    void setValue(Cell cell, Object value) {

        if (value == null) {
            cell.setBlank();
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue((LocalDateTime) value);
        } else if (value instanceof LocalDate) {
            cell.setCellValue((LocalDate) value);
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private CellStyle cellStyle(String columnName, Class<?> valueType) {
        // explicit styles take precedence
        CellStyle explicit = stylesByColumn.get(columnName);
        return explicit != null ? explicit : cellStyle(valueType);
    }

    private CellStyle cellStyle(Class<?> valueType) {

        if (valueType == null) {
            return null;
        }

        // Not using "computeIfAbsent" to be able to store NULLs explicitly for missing styles

        CellStyle style = stylesByType.get(valueType);
        if (style != null || stylesByType.containsKey(valueType)) {
            return style;
        }

        CellStyle newStyle = createCellStyle(valueType);
        stylesByType.put(valueType, newStyle);
        return newStyle;
    }

    private CellStyle createCellStyle(Class<?> valueType) {
        if (LocalDateTime.class.equals(valueType)) {
            return localDateTimeStyle();
        } else if (LocalDate.class.equals(valueType)) {
            return localDateStyle();
        } else if (Date.class.equals(valueType)) {
            return localDateTimeStyle();
        } else {
            return null;
        }
    }

    private CellStyle localDateStyle() {
        return stylesByType.computeIfAbsent(LocalDate.class, t -> {

            // Excel build-int formats (as defined in POI BuiltinFormats) do contain international date formats..
            // TODO: should we introduce custom formats?

            short f = sheet.getWorkbook().createDataFormat().getFormat("m/d/yy");
            CellStyle style = sheet.getWorkbook().createCellStyle();
            style.setDataFormat(f);
            return style;
        });
    }

    private CellStyle localDateTimeStyle() {
        return stylesByType.computeIfAbsent(LocalDateTime.class, t -> {

            // Excel build-int formats (as defined in POI BuiltinFormats) do contain international date formats..
            // TODO: should we introduce custom formats?

            short f = sheet.getWorkbook().createDataFormat().getFormat("m/d/yy h:mm");
            CellStyle style = sheet.getWorkbook().createCellStyle();
            style.setDataFormat(f);
            return style;
        });
    }
}
