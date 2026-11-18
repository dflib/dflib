package org.dflib.excel;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExcelLoader_OpenWorkbookTest {

    @ParameterizedTest
    @CsvSource({
            "one-sheet.xlsx, org.dflib.excel.XlsxWorkbookData",
            "one-sheet.xls, org.dflib.excel.PoiWorkbookData"})
    public void fromFile(String source, Class<?> expectedType) throws URISyntaxException {
        File file = new File(getClass().getResource(source).toURI());

        try (WorkbookData wb = new ExcelLoader().openWorkbook(file)) {
            assertEquals(expectedType, wb.getClass());
        }
    }

    @ParameterizedTest
    @CsvSource({
            "one-sheet.xlsx, org.dflib.excel.XlsxWorkbookData",
            "one-sheet.xls, org.dflib.excel.PoiWorkbookData"})
    public void fromStream(String source, Class<?> expectedType) throws IOException {
        try (InputStream in = getClass().getResourceAsStream(source);
             WorkbookData wb = new ExcelLoader().openWorkbook(in)) {
            assertEquals(expectedType, wb.getClass());
        }
    }
}
