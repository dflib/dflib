package org.dflib.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dflib.DataFrame;
import org.dflib.junit.DataFrameAsserts;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Loading the cached values of formula cells produced by POI formula evaluation.
 */
public class ExcelLoader_LoadFormulasTest {

    @TempDir
    static Path tempDir;

    static byte[] xlsx;
    static Path xlsxFile;

    @BeforeAll
    static void createWorkbook() throws IOException {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {

            CellStyle dateStyle = wb.createCellStyle();
            dateStyle.setDataFormat(wb.createDataFormat().getFormat("m/d/yy"));

            Sheet data = wb.createSheet("Data");
            for (int i = 0; i < 3; i++) {
                Row r = data.createRow(i);
                r.createCell(0).setCellValue(i + 1);
                r.createCell(1).setCellValue("s" + i);
            }

            Sheet formulas = wb.createSheet("Formulas");
            Row header = formulas.createRow(0);
            String[] labels = {"num", "str", "bool", "err", "date"};
            for (int i = 0; i < labels.length; i++) {
                header.createCell(i).setCellValue(labels[i]);
            }

            for (int i = 0; i < 3; i++) {
                Row r = formulas.createRow(i + 1);
                int dataRow = i + 1;

                r.createCell(0).setCellFormula("Data!A" + dataRow + "*10");
                r.createCell(1).setCellFormula("Data!B" + dataRow + "&\"x\"");
                r.createCell(2).setCellFormula("Data!A" + dataRow + ">1");
                r.createCell(3).setCellFormula("1/0");

                Cell date = r.createCell(4);
                date.setCellFormula("DATE(2020,1," + (18 + i) + ")");
                date.setCellStyle(dateStyle);
            }

            wb.getCreationHelper().createFormulaEvaluator().evaluateAll();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wb.write(out);
            xlsx = out.toByteArray();
        }

        xlsxFile = tempDir.resolve("formulas.xlsx");
        Files.write(xlsxFile, xlsx);
    }

    @Test
    public void fromStream() {
        DataFrame df = new ExcelLoader().firstRowAsHeader().loadSheet(new ByteArrayInputStream(xlsx), "Formulas");
        assertFormulas(df);
    }

    @Test
    public void fromFile() {
        DataFrame df = new ExcelLoader().firstRowAsHeader().loadSheet(xlsxFile, "Formulas");
        assertFormulas(df);
    }

    @Test
    public void fromFile_ByPosition() {
        DataFrame df = new ExcelLoader().firstRowAsHeader().loadSheet(xlsxFile, 1);
        assertFormulas(df);
    }

    @Test
    public void allSheets() {
        Map<String, DataFrame> data = TestXlsx.loadAndCompare(xlsx, () -> new ExcelLoader().firstRowAsHeader());
        assertFormulas(data.get("Formulas"));
    }

    private static void assertFormulas(DataFrame df) {
        new DataFrameAsserts(df, "num", "str", "bool", "err", "date")
                .expectHeight(3)
                .expectRow(0, 10., "s0x", false, null, LocalDateTime.of(2020, 1, 18, 0, 0))
                .expectRow(1, 20., "s1x", true, null, LocalDateTime.of(2020, 1, 19, 0, 0))
                .expectRow(2, 30., "s2x", true, null, LocalDateTime.of(2020, 1, 20, 0, 0));
    }
}
