package org.dflib.excel;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.dflib.DataFrame;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class ExcelLoader {

    private final SheetLoader defaultSheetLoader;
    private final Map<String, SheetLoader> sheetLoaders;

    // TODO: add builder method to capture Excel file password

    public ExcelLoader() {
        this.defaultSheetLoader = new SheetLoader();
        this.sheetLoaders = new HashMap<>();
    }

    /**
     * Generates header index from the first row. If {@link #offset(int)} is in use, this will be the first non-skipped
     * row.
     */
    public ExcelLoader firstRowAsHeader() {
        defaultSheetLoader.firstRowAsHeader();
        return this;
    }

    /**
     * Sets configuration specific to a given sheet.
     */
    public ExcelLoader sheet(String sheetName, SheetLoader sheetLoader) {
        sheetLoaders.put(sheetName, sheetLoader);
        return this;
    }

    /**
     * Skips the specified number of rows. This counter only applies to non-phantom rows. I.e., those rows that have
     * non-empty cells. Phantom rows are skipped automatically.
     */
    public ExcelLoader offset(int len) {
        defaultSheetLoader.offset(len);
        return this;
    }

    /**
     * Limits the max number of rows to the provided value. This counter only applies to non-phantom rows. I.e., those
     * rows that have non-empty cells. Phantom rows are skipped automatically.
     */
    public ExcelLoader limit(int len) {
        defaultSheetLoader.limit(len);
        return this;
    }


    public DataFrame loadSheet(Sheet sheet) {
        SheetLoader storedSl = sheetLoaders.get(sheet.getSheetName());

        // note that the stored SheetLoader is mutated here
        SheetLoader sl = storedSl != null ? storedSl.mergeWith(defaultSheetLoader) : defaultSheetLoader;

        return sl.load(sheet);
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
            throw new RuntimeException("Error reading file: " + file, e);
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
}
