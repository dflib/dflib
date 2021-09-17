package com.nhl.dflib.excel;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.DataFrameByRowBuilder;
import com.nhl.dflib.Index;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @since 0.13
 */
public class ExcelLoader {

    // TODO: add builder method to capture Excel file password
    // TODO: add builder method for specifying named sheets to load
    // TODO: add builder method to use the first row values as index

    public Map<String, DataFrame> load(InputStream in) {
        return convertToDataFrames(loadWorkbook(in));
    }

    protected Workbook loadWorkbook(InputStream in) {
        Objects.requireNonNull(in, "Null input stream");

        try {
            return WorkbookFactory.create(in);
        } catch (IOException e) {
            throw new RuntimeException("Error reading Excel data", e);
        }
    }

    protected Map<String, DataFrame> convertToDataFrames(Workbook wb) {
        Map<String, DataFrame> data = new HashMap<>();

        for (Sheet sh : wb) {
            data.put(sh.getSheetName(), convertToDataFrame(sh));
        }

        return data;
    }

    protected DataFrame convertToDataFrame(Sheet sh) {

        // should this ever happen with POI?
        if (sh.getPhysicalNumberOfRows() == 0) {
            return DataFrame.newFrame().empty();
        }

        int[] minMaxColumns = minMaxColumns(sh);
        int start = minMaxColumns[0];
        int end = minMaxColumns[1];

        Index columns = columns(start, end);
        DataFrameByRowBuilder builder = DataFrame.newFrame(columns).byRow();

        Object[] buffer = new Object[columns.size()];

        for (Row row : sh) {

            // navigating through all buffer positions, populating with values or nulls.
            for (int i = 0; i < buffer.length; i++) {
                Cell cell = row.getCell(start + i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);

                // resetting buffer value to null is important, as the buffer is reused between rows
                buffer[i] = cell != null ? value(cell) : null;
            }

            for (Cell cell : row) {
                buffer[cell.getColumnIndex() - start] = value(cell);
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

    private Index columns(int start, int end) {
        int len = end - start;
        String[] names = new String[len];

        for (int i = 0; i < len; i++) {
            names[i] = CellReference.convertNumToColString(start + i);
        }

        return Index.forLabels(names);
    }

    private int[] minMaxColumns(Sheet sh) {
        int min = Short.MAX_VALUE + 1;
        int max = Short.MIN_VALUE - 1;

        for (Row r : sh) {
            min = Math.min(r.getFirstCellNum(), min);
            max = Math.max(r.getLastCellNum(), max);
        }

        return new int[]{min, max};
    }
}
