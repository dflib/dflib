package com.nhl.dflib.excel;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @since 0.14
 */
public class ExcelSaver {

    private boolean createMissingDirs;
    private boolean indexAsTopRow;

    public ExcelSaver() {
        this.createMissingDirs = false;
        this.indexAsTopRow = true;
    }

    /**
     * Instructs the saver to create any missing directories in the file path.
     *
     * @return this saver instance
     */
    public ExcelSaver createMissingDirs() {
        this.createMissingDirs = true;
        return this;
    }

    public ExcelSaver noHeader() {
        this.indexAsTopRow = false;
        return this;
    }

    /**
     * Saves a DataFrame into an Excel file in a named sheet. If the file already exists, only the named sheet is
     * overridden. Other sheets will remain unchanged.
     */
    public void saveSheet(DataFrame df, File file, String sheetName) {

        if (createMissingDirs) {
            File dir = file.getParentFile();
            if (dir != null) {
                dir.mkdirs();
            }
        }

        if (file.exists()) {
            mergeToFile(df, file, sheetName);
        } else {
            writeToFile(df, file, sheetName);
        }
    }

    public void saveSheet(DataFrame df, Path filePath, String sheetName) {
        saveSheet(df, filePath.toFile(), sheetName);
    }

    public void saveSheet(DataFrame df, String fileName, String sheetName) {
        saveSheet(df, new File(fileName), sheetName);
    }

    public void saveSheet(DataFrame df, OutputStream out, String sheetName) {
        writeToOut(df, () -> out, () -> WorkbookFactory.create(true), sheetName);
    }

    protected void mergeToFile(DataFrame df, File file, String sheetName) {
        writeToOut(df, () -> {

            // Delete the underlying file. Per POIXMLDocument docs:
            // "if the Document was opened from a File rather than an InputStream, you must write out to a different
            // file, overwriting via an OutputStream isn't possible"...

            // TODO: Is it bad for any reason?

            file.delete();
            return new FileOutputStream(file);

        }, () -> WorkbookFactory.create(file), sheetName);
    }

    protected void writeToFile(DataFrame df, File file, String sheetName) {
        writeToOut(df, () -> new FileOutputStream(file), () -> WorkbookFactory.create(true), sheetName);
    }

    protected void writeToOut(DataFrame df, ThrowingSupplier<OutputStream> out, ThrowingSupplier<Workbook> workbookSupplier, String sheetName) {
        try (Workbook wb = workbookSupplier.get()) {
            mergeToWorkbook(df, wb, sheetName);

            try (OutputStream os = out.get()) {
                wb.write(os);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing to an Excel stream: " + e.getMessage(), e);
        }
    }

    protected void mergeToWorkbook(DataFrame df, Workbook wb, String sheetName) {

        Objects.requireNonNull(sheetName);

        Sheet existingSheet = wb.getSheet(sheetName);
        if (existingSheet != null) {
            wb.removeSheetAt(wb.getSheetIndex(existingSheet));
        }

        Sheet sheet = wb.createSheet(sheetName);

        int w = df.width();
        int startDataRow = indexAsTopRow ? 1 : 0;

        if (indexAsTopRow) {
            Index index = df.getColumnsIndex();
            Row row = sheet.createRow(0);

            Font topRowFont = wb.createFont();
            topRowFont.setBold(true);

            CellStyle topRowStyle = wb.createCellStyle();
            topRowStyle.setFont(topRowFont);
            topRowStyle.setAlignment(HorizontalAlignment.CENTER);

            for (int i = 0; i < w; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(index.getLabel(i));
                cell.setCellStyle(topRowStyle);
            }
        }

        // TODO: is this redundant? Can this be looked up on the Worksheet?
        Map<Class, CellStyle> styles = new HashMap<>();

        for (int i = 0; i < w; i++) {
            Series<?> column = df.getColumn(i);
            Class<?> type = column.getNominalType();
            if (Boolean.TYPE.equals(type)) {
                updateBooleanColumn(sheet, i, startDataRow, (Series<Boolean>) column);
            } else if (Integer.TYPE.equals(type)) {
                updateIntColumn(sheet, i, startDataRow, (Series<Integer>) column);
            } else if (Long.TYPE.equals(type)) {
                updateLongColumn(sheet, i, startDataRow, (Series<Long>) column);
            } else if (Double.TYPE.equals(type)) {
                updateDoubleColumn(sheet, i, startDataRow, (Series<Double>) column);
            } else {
                updateColumn(sheet, i, startDataRow, column, styles);
            }
        }
    }

    private void updateBooleanColumn(Sheet sheet, int col, int startRow, Series<Boolean> data) {
        int len = data.size();

        for (int i = 0; i < len; i++) {
            Row existingRow = sheet.getRow(startRow + i);
            Row row = existingRow != null ? existingRow : sheet.createRow(startRow + i);
            Cell cell = row.createCell(col);
            cell.setCellValue(data.get(i));
        }
    }

    private void updateIntColumn(Sheet sheet, int col, int startRow, Series<Integer> data) {

        int len = data.size();

        for (int i = 0; i < len; i++) {
            Row existingRow = sheet.getRow(startRow + i);
            Row row = existingRow != null ? existingRow : sheet.createRow(startRow + i);
            Cell cell = row.createCell(col);
            cell.setCellValue(data.get(i));
        }
    }

    private void updateLongColumn(Sheet sheet, int col, int startRow, Series<Long> data) {
        int len = data.size();

        for (int i = 0; i < len; i++) {
            Row existingRow = sheet.getRow(startRow + i);
            Row row = existingRow != null ? existingRow : sheet.createRow(startRow + i);
            Cell cell = row.createCell(col);
            cell.setCellValue(data.get(i));
        }
    }

    private void updateDoubleColumn(Sheet sheet, int col, int startRow, Series<Double> data) {
        int len = data.size();

        for (int i = 0; i < len; i++) {
            Row existingRow = sheet.getRow(startRow + i);
            Row row = existingRow != null ? existingRow : sheet.createRow(startRow + i);
            Cell cell = row.createCell(col);
            cell.setCellValue(data.get(i));
        }
    }

    private void updateColumn(Sheet sheet, int col, int startRow, Series<?> data, Map<Class, CellStyle> styleCache) {

        int len = data.size();

        for (int i = 0; i < len; i++) {
            Row existingRow = sheet.getRow(startRow + i);
            Row row = existingRow != null ? existingRow : sheet.createRow(startRow + i);
            Cell cell = row.createCell(col);
            setValue(cell, data.get(i), styleCache);
        }
    }

    private void setValue(Cell cell, Object value, Map<Class, CellStyle> styleCache) {

        if (value == null) {
            cell.setBlank();
        } else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof LocalDateTime) {
            cell.setCellValue((LocalDateTime) value);
            cell.setCellStyle(createLocalDateTimeStyle(cell.getSheet(), styleCache));
        } else if (value instanceof LocalDate) {
            cell.setCellValue((LocalDate) value);
            cell.setCellStyle(createLocalDateStyle(cell.getSheet(), styleCache));
        } else if (value instanceof Date) {
            cell.setCellValue((Date) value);
            cell.setCellStyle(createLocalDateTimeStyle(cell.getSheet(), styleCache));
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private CellStyle createLocalDateStyle(Sheet sheet, Map<Class, CellStyle> styleCache) {
        return styleCache.computeIfAbsent(LocalDate.class, t -> {

            // Excel build-int formats (as defined in POI BuiltinFormats) do contain international date formats..
            // TODO: should we introduce custom formats?

            short f = sheet.getWorkbook().createDataFormat().getFormat("m/d/yy");
            CellStyle style = sheet.getWorkbook().createCellStyle();
            style.setDataFormat(f);
            return style;
        });
    }

    private CellStyle createLocalDateTimeStyle(Sheet sheet, Map<Class, CellStyle> styleCache) {
        return styleCache.computeIfAbsent(LocalDateTime.class, t -> {

            // Excel build-int formats (as defined in POI BuiltinFormats) do contain international date formats..
            // TODO: should we introduce custom formats?

            short f = sheet.getWorkbook().createDataFormat().getFormat("m/d/yy h:mm");
            CellStyle style = sheet.getWorkbook().createCellStyle();
            style.setDataFormat(f);
            return style;
        });
    }

    protected interface ThrowingSupplier<T> {
        T get() throws IOException;
    }

}
