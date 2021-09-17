package com.nhl.dflib.excel;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.DataFrameByRowBuilder;
import com.nhl.dflib.Index;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @since 0.13
 */
public class ExcelLoader {

    // TODO: add builder method to capture Excel file password
    // TODO: add builder method for specifying named sheets to load
    // TODO: add builder method to use the first row values as index

    public Map<String, DataFrame> load(File file) {
        // loading from file is optimized, so do not call "load(InputStream in)", and use a separate path
        try (Workbook wb = loadWorkbook(file)) {
            return toDataFrames(wb);
        } catch (IOException e) {
            throw new RuntimeException("Error closing Excel workbook loaded from " + file.getName(), e);
        }
    }

    public Map<String, DataFrame> load(InputStream in) {
        try (Workbook wb = loadWorkbook(in)) {
            return toDataFrames(wb);
        } catch (IOException e) {
            throw new RuntimeException("Error closing Excel workbook", e);
        }
    }

    public Map<String, DataFrame> load(Path path) {
        return load(path.toFile());
    }

    public Map<String, DataFrame> load(String filePath) {
        return load(new File(filePath));
    }

    private Workbook loadWorkbook(File file) {
        // loading from file is optimized, so do not call "loadWorkbook(InputStream in)", and use a separate path
        Objects.requireNonNull(file, "Null file");

        try {
            return WorkbookFactory.create(file);
        } catch (IOException e) {
            throw new RuntimeException("Error reading Excel data", e);
        }
    }

    private Workbook loadWorkbook(InputStream in) {
        Objects.requireNonNull(in, "Null input stream");

        try {
            return WorkbookFactory.create(in);
        } catch (IOException e) {
            throw new RuntimeException("Error reading Excel data", e);
        }
    }

    private Map<String, DataFrame> toDataFrames(Workbook wb) {
        Map<String, DataFrame> data = new HashMap<>();

        for (Sheet sh : wb) {
            data.put(sh.getSheetName(), toDataFrame(sh));
        }

        return data;
    }

    private DataFrame toDataFrame(Sheet sh) {

        // should this ever happen with POI?
        if (sh.getPhysicalNumberOfRows() == 0) {
            return DataFrame.newFrame().empty();
        }

        SheetRange range = SheetRange.valuesRange(sh);
        DataFrameByRowBuilder builder = DataFrame.newFrame(range.columns()).byRow();

        Object[] buffer = new Object[range.width];

        // Don't skip empty rows or columns in the middle of a range, but truncate leading empty rows and columns
        for (int r = 0; r < range.height; r++) {

            Row row = sh.getRow(range.startRow + r);
            if (row == null) {
                Arrays.fill(buffer, null);
            } else {

                for (int c = 0; c < range.width; c++) {
                    Cell cell = row.getCell(range.startCol + c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

                    // resetting buffer value to null for missing cells is important, as the buffer is reused between rows
                    buffer[c] = cell != null ? value(cell) : null;
                }

                for (Cell cell : row) {
                    buffer[cell.getColumnIndex() - range.startCol] = value(cell);
                }
            }

            builder.addRow(buffer);
        }

        return builder.create();
    }

    private Object value(Cell cell) {

        CellType type = cell.getCellType() == CellType.FORMULA
                ? cell.getCachedFormulaResultType() : cell.getCellType();

        switch (type) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                // TODO: check the actual format to return LocalDate, LocalDateTime or smth else
                return DateUtil.isCellDateFormatted(cell)
                        ? cell.getLocalDateTimeCellValue()
                        : cell.getNumericCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                throw new IllegalStateException("FORMULA is unexpected as a FORMULA cell result");
            case BLANK:
            case ERROR:
            default:
                return null;
        }
    }


    static class SheetRange {

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
            this.width = endCol - startCol;
            this.height = endRow - startRow;
        }

        static SheetRange valuesRange(Sheet sh) {

            int startCol = Short.MAX_VALUE + 1;
            int endCol = Short.MIN_VALUE - 1;
            int startRow = Integer.MAX_VALUE;
            int endRow = Integer.MIN_VALUE;

            for (Row r : sh) {

                startRow = Math.min(r.getRowNum(), startRow);
                endRow = Math.max(r.getRowNum(), endRow);

                startCol = Math.min(r.getFirstCellNum(), startCol);
                endCol = Math.max(r.getLastCellNum(), endCol);
            }

            // only non-empty Sheets are allowed here...
            if (startCol > endCol || startRow > endRow) {
                throw new IllegalArgumentException("Empty sheet");
            }

            return new SheetRange(
                    startCol,
                    // "endCol" already points past the last column
                    endCol,
                    startRow,
                    // as "endRow" is an index, we must increment it to point past the last row
                    endRow + 1);
        }

        Index columns() {
            String[] names = new String[width];

            for (int i = 0; i < width; i++) {
                names[i] = CellReference.convertNumToColString(startCol + i);
            }

            return Index.forLabels(names);
        }
    }
}
