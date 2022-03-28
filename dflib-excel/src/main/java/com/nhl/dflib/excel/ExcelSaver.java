package com.nhl.dflib.excel;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.LongSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.row.RowProxy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ExcelSaver {

    private boolean createMissingDirs;
    private boolean printHeader;

    public ExcelSaver() {
        this.printHeader = true;
    }

    /**
     * Instructs the saver to create any missing directories in the file path.
     *
     * @return this saver instance
     * @since 0.12
     */
    public ExcelSaver createMissingDirs() {
        this.createMissingDirs = true;
        return this;
    }

    /**
     * Instructs the saver to omit saving the Index of a DataFrame. By default, the Index will be saved as a first row
     * in a sheet.
     *
     * @return this saver instance
     * @since 0.12
     */
    public ExcelSaver noHeader() {
        this.printHeader = false;
        return this;
    }

    /**
     * @since 0.12
     */
    public void save(Map<String, DataFrame> dataFrames, File file) {
        if (createMissingDirs) {
            File dir = file.getParentFile();
            if (dir != null) {
                dir.mkdirs();
            }
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        dataFrames.forEach((sheetName, dataFrame) -> {
            createSheet(workbook, sheetName, dataFrame);
        });

        try (OutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
        } catch (IOException e) {
            throw new RuntimeException("Error writing EXCEL to " + file + ": " + e.getMessage(), e);
        }
    }

    public void save(String sheetName, DataFrame dataFrame, File file) {
        Map<String, DataFrame> sheetNameToDF = new HashMap<>();
        sheetNameToDF.put(sheetName, dataFrame);
        save(sheetNameToDF, file);
    }

    public void save(String sheetName, DataFrame dataFrame, Path path) {
        Map<String, DataFrame> sheetNameToDF = new HashMap<>();
        sheetNameToDF.put(sheetName, dataFrame);
        save(sheetNameToDF, path);
    }

    public void save(String sheetName, DataFrame dataFrame, String file) {
        Map<String, DataFrame> sheetNameToDF = new HashMap<>();
        sheetNameToDF.put(sheetName, dataFrame);
        save(sheetNameToDF, file);
    }
    /**
     * @since 0.12
     */
    public void save(Map<String, DataFrame> df, Path filePath) {
        save(df, filePath.toFile());
    }

    /**
     * @since 0.12
     */
    public void save(Map<String, DataFrame> df, String fileName) {
        save(df, new File(fileName));
    }

    private void createSheet(XSSFWorkbook workbook, String sheetName, DataFrame dataFrame) {
        final XSSFSheet sheet = workbook.createSheet(sheetName);
        int rowCount = 0;
        if (printHeader) {
            rowCount = writeSheetHeader(dataFrame, sheet, rowCount);
        }

        for (RowProxy rowProxy : dataFrame) {
            rowCount = writeSheetRow(dataFrame, sheet, rowCount, rowProxy);
        }
    }

    private int writeSheetRow(DataFrame dataFrame, XSSFSheet sheet, int rowCount, RowProxy rowProxy) {
        Row row = sheet.createRow(++rowCount);
        for (int columnIndex = 0; columnIndex < dataFrame.getColumnsIndex().size(); columnIndex++) {
            Cell cell = row.createCell(columnIndex);
            Series<?> column = dataFrame.getColumn(columnIndex);
            Object cellValue = rowProxy.get(columnIndex);
            if (column instanceof IntSeries
                    || column instanceof LongSeries
                    || column instanceof DoubleSeries) {
                Number numberCellValue = (Number) cellValue;
                cell.setCellValue(numberCellValue.doubleValue());
            } else if (column instanceof BooleanSeries) {
                Boolean boolCellValue = (Boolean) cellValue;
                cell.setCellValue(boolCellValue != null && boolCellValue);
            } else {
                cell.setCellValue(cellValue == null ? "" : cellValue.toString());
            }
        }
        return rowCount;
    }

    private int writeSheetHeader(DataFrame dataFrame, XSSFSheet sheet, int rowCount) {
        Row row = sheet.createRow(++rowCount);
        for (int col = 0; col < dataFrame.getColumnsIndex().size(); col++) {
            Cell cell = row.createCell(col);
            cell.setCellValue(dataFrame.getColumnsIndex().getLabel(col));
        }
        return rowCount;
    }


}
