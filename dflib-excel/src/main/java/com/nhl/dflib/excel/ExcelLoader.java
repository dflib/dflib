package com.nhl.dflib.excel;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Extractor;
import com.nhl.dflib.Index;
import com.nhl.dflib.builder.DataFrameAppender;
import com.nhl.dflib.builder.ObjectHolder;
import com.nhl.dflib.builder.ValueHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @since 0.13
 */
public class ExcelLoader {

    // TODO: add builder method to capture Excel file password

    private boolean firstRowAsHeader;

    /**
     * @since 0.14
     */
    public ExcelLoader firstRowAsHeader() {
        this.firstRowAsHeader = true;
        return this;
    }

    /**
     * @since 0.14
     */
    public DataFrame loadSheet(Sheet sheet) {

        // Don't skip empty rows or columns in the middle of a range, but truncate leading empty rows and columns
        SheetRange range = SheetRange.valuesRange(sheet);
        if (range.isEmpty()) {
            return DataFrame.empty();
        }

        if (range.height == 0) {
            return DataFrame.empty(range.columns());
        }

        Extractor<Row, ?>[] extractors = createExtractors(range.startCol, range.width);

        Row row0 = getRow(sheet, range, 0);
        Index index = firstRowAsHeader ? createIndex(row0, extractors) : range.columns();

        DataFrameAppender<Row> builder = DataFrame.builder(extractors).columnIndex(index).appendData();

        if (!firstRowAsHeader) {
            builder.append(row0);
        }

        for (int r = 1; r < range.height; r++) {
            builder.append(getRow(sheet, range, r));
        }

        return builder.build();
    }

    public DataFrame loadSheet(InputStream in, String sheetName) {
        try (Workbook wb = loadWorkbook(in)) {
            Sheet sheet = wb.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("No sheet '" + sheetName + "' in workbook");
            }
            return loadSheet(sheet);
        } catch (IOException e) {
            throw new RuntimeException("Error closing Excel workbook", e);
        }
    }

    public DataFrame loadSheet(File file, String sheetName) {
        // loading from file is optimized, so do not call "load(InputStream in)", and use a separate path
        try (Workbook wb = loadWorkbook(file)) {
            Sheet sheet = wb.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("No sheet '" + sheetName + "' in workbook loaded from " + file.getPath());
            }
            return loadSheet(sheet);
        } catch (IOException e) {
            throw new RuntimeException("Error closing Excel workbook loaded from " + file.getPath(), e);
        }
    }

    public DataFrame loadSheet(Path path, String sheetName) {
        return loadSheet(path.toFile(), sheetName);
    }

    public DataFrame loadSheet(String filePath, String sheetName) {
        return loadSheet(new File(filePath), sheetName);
    }

    public DataFrame loadSheet(InputStream in, int sheetNum) {
        try (Workbook wb = loadWorkbook(in)) {
            Sheet sheet = wb.getSheetAt(sheetNum);
            if (sheet == null) {
                throw new RuntimeException("No sheet " + sheetNum + " in workbook");
            }
            return loadSheet(sheet);
        } catch (IOException e) {
            throw new RuntimeException("Error closing Excel workbook", e);
        }
    }

    public DataFrame loadSheet(File file, int sheetNum) {
        // loading from file is optimized, so do not call "load(InputStream in)", and use a separate path
        try (Workbook wb = loadWorkbook(file)) {
            Sheet sheet = wb.getSheetAt(sheetNum);
            if (sheet == null) {
                throw new RuntimeException("No sheet " + sheetNum + " in workbook loaded from " + file.getPath());
            }
            return loadSheet(sheet);
        } catch (IOException e) {
            throw new RuntimeException("Error closing Excel workbook loaded from " + file.getPath(), e);
        }
    }

    public DataFrame loadSheet(Path path, int sheetNum) {
        return loadSheet(path.toFile(), sheetNum);
    }

    public DataFrame loadSheet(String filePath, int sheetNum) {
        return loadSheet(new File(filePath), sheetNum);
    }

    /**
     * @since 0.14
     */
    public Map<String, DataFrame> load(Workbook wb) {

        // preserve sheet ordering
        Map<String, DataFrame> data = new LinkedHashMap<>();

        for (Sheet sh : wb) {
            data.put(sh.getSheetName(), loadSheet(sh));
        }

        return data;
    }

    public Map<String, DataFrame> load(InputStream in) {
        try (Workbook wb = loadWorkbook(in)) {
            return load(wb);
        } catch (IOException e) {
            throw new RuntimeException("Error closing Excel workbook", e);
        }
    }

    public Map<String, DataFrame> load(File file) {
        // loading from file is optimized, so do not call "load(InputStream in)", and use a separate path
        try (Workbook wb = loadWorkbook(file)) {
            return load(wb);
        } catch (IOException e) {
            throw new RuntimeException("Error closing Excel workbook loaded from " + file.getPath(), e);
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

    private Extractor<Row, ?>[] createExtractors(int startCol, int w) {
        Extractor<Row, ?>[] extractors = new Extractor[w];

        for (int i = 0; i < w; i++) {
            int pos = startCol + i;
            extractors[i] = Extractor.$col(r -> value(r, pos));
        }

        return extractors;
    }

    private Index createIndex(Row row, Extractor<Row, ?>[] extractors) {

        int w = extractors.length;
        String[] labels = new String[w];

        if (row != null) {
            ValueHolder holder = new ObjectHolder<>();
            for (int i = 0; i < w; i++) {
                extractors[i].extractAndStore(row, holder);
                labels[i] = holder.get() != null ? holder.get().toString() : "";
            }
        } else {
            Arrays.fill(labels, "");
        }

        return Index.forLabels(labels);
    }

    private Row getRow(Sheet sh, SheetRange range, int rowOffset) {
        return sh.getRow(range.startRow + rowOffset);
    }

    private Object value(Row row, int pos) {
        if (row == null) {
            return null;
        }

        Cell cell = row.getCell(pos, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return cell != null ? value(cell) : null;
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
}
