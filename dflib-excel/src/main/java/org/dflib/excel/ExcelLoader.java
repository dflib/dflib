package org.dflib.excel;

import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.dflib.DataFrame;
import org.dflib.ByteSource;

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
        return sheetLoader(sheet.getSheetName()).load(sheet);
    }

    /**
     * @since 1.1.0
     */
    public DataFrame loadSheet(ByteSource src, String sheetName) {
        try (WorkbookData wb = src.processStream(this::openWorkbook)) {
            return loadSheet(wb, sheetName, "");
        }
    }

    public DataFrame loadSheet(InputStream in, String sheetName) {
        try (WorkbookData wb = openWorkbook(in)) {
            return loadSheet(wb, sheetName, "");
        }
    }

    public DataFrame loadSheet(File file, String sheetName) {
        try (WorkbookData wb = openWorkbook(file)) {
            return loadSheet(wb, sheetName, " loaded from " + file.getPath());
        }
    }

    public DataFrame loadSheet(Path path, String sheetName) {
        return loadSheet(path.toFile(), sheetName);
    }

    public DataFrame loadSheet(String filePath, String sheetName) {
        return loadSheet(new File(filePath), sheetName);
    }

    /**
     * @since 1.1.0
     */
    public DataFrame loadSheet(ByteSource src, int sheetNum) {
        try (WorkbookData wb = src.processStream(this::openWorkbook)) {
            return loadSheet(wb, sheetNum, "");
        }
    }

    public DataFrame loadSheet(InputStream in, int sheetNum) {
        try (WorkbookData wb = openWorkbook(in)) {
            return loadSheet(wb, sheetNum, "");
        }
    }

    public DataFrame loadSheet(File file, int sheetNum) {
        try (WorkbookData wb = openWorkbook(file)) {
            return loadSheet(wb, sheetNum, " loaded from " + file.getPath());
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

    /**
     * @since 1.1.0
     */
    public Map<String, DataFrame> load(ByteSource src) {
        try (WorkbookData wb = src.processStream(this::openWorkbook)) {
            return loadAll(wb);
        }
    }

    public Map<String, DataFrame> load(InputStream in) {
        try (WorkbookData wb = openWorkbook(in)) {
            return loadAll(wb);
        }
    }

    public Map<String, DataFrame> load(File file) {
        try (WorkbookData wb = openWorkbook(file)) {
            return loadAll(wb);
        }
    }

    public Map<String, DataFrame> load(Path path) {
        return load(path.toFile());
    }

    public Map<String, DataFrame> load(String filePath) {
        return load(new File(filePath));
    }

    private SheetLoader sheetLoader(String sheetName) {
        SheetLoader storedSl = sheetLoaders.get(sheetName);
        return storedSl != null ? storedSl.mergeWith(defaultSheetLoader) : defaultSheetLoader;
    }

    private Map<String, DataFrame> loadAll(WorkbookData wb) {

        // preserve sheet ordering
        Map<String, DataFrame> data = new LinkedHashMap<>();

        int len = wb.sheetCount();
        for (int i = 0; i < len; i++) {
            data.put(wb.sheetName(i), loadSheetAt(wb, i));
        }

        return data;
    }

    private DataFrame loadSheet(WorkbookData wb, String sheetName, String location) {
        int sheetNum = wb.sheetIndex(sheetName);
        if (sheetNum < 0) {
            throw new RuntimeException("No sheet '" + sheetName + "' in workbook" + location);
        }

        return loadSheetAt(wb, sheetNum);
    }

    private DataFrame loadSheet(WorkbookData wb, int sheetNum, String location) {
        if (sheetNum < 0 || sheetNum >= wb.sheetCount()) {
            throw new RuntimeException("No sheet " + sheetNum + " in workbook" + location);
        }

        return loadSheetAt(wb, sheetNum);
    }

    private DataFrame loadSheetAt(WorkbookData wb, int sheetNum) {
        SheetLoader sl = sheetLoader(wb.sheetName(sheetNum));
        return sl.load(wb.sheet(sheetNum, sl.rowOffset(), sl.rowLimit()));
    }

    WorkbookData openWorkbook(File file) {
        // loading from file is optimized, so do not call "openWorkbook(InputStream in)", and use a separate path
        Objects.requireNonNull(file, "Null file");

        try {

            // XLSX files are read with a streaming reader; everything else (XLS, encrypted XLSX) - with POI usermodel
            return FileMagic.valueOf(file) == FileMagic.OOXML
                    ? XlsxWorkbookData.open(file)
                    : new PoiWorkbookData(WorkbookFactory.create(file, null, true));
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + file, e);
        }
    }

    WorkbookData openWorkbook(InputStream in) {
        Objects.requireNonNull(in, "Null input stream");

        // ensure the stream supports mark/reset needed to check the format
        InputStream markableIn = FileMagic.prepareToCheckMagic(in);

        try {

            // XLSX files are read with a streaming reader; everything else (XLS, encrypted XLSX) - with POI usermodel
            return FileMagic.valueOf(markableIn) == FileMagic.OOXML
                    ? XlsxWorkbookData.open(markableIn)
                    : new PoiWorkbookData(WorkbookFactory.create(markableIn));
        } catch (IOException e) {
            throw new RuntimeException("Error reading Excel data", e);
        }
    }
}
