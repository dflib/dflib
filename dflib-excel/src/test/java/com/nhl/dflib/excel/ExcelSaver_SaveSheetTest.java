package com.nhl.dflib.excel;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.LongSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.junit5.DataFrameAsserts;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ExcelSaver_SaveSheetTest extends BaseExcelTest {

    @Test
    public void testSaveSheet_Types() {

        File file = new File(outPath("testSave_Types.xlsx"));

        DataFrame df = DataFrame.newFrame("bool", "int", "long", "double", "string", "mix")
                .columns(
                        BooleanSeries.forBooleans(true, false, false, true),
                        IntSeries.forInts(1, -20003, 23, 65),
                        LongSeries.forLongs(0, 34567890324L, -9L, 15),
                        DoubleSeries.forDoubles(-0.1, 8.45, 7.0001, 67.),
                        Series.forData("a", "B", "", null),
                        Series.forData(new BigDecimal("0.001"), null, LocalDateTime.of(2000, 3, 4, 14, 2, 3), LocalDate.of(2003, 5, 7))
                );

        Excel.saver().saveSheet(df, file, "s1");

        DataFrame reload = Excel.loader().firstRowAsHeader().loadSheet(file, "s1");
        new DataFrameAsserts(reload, "bool", "int", "long", "double", "string", "mix")
                .expectHeight(4)
                .expectColumn(0, true, false, false, true)

                // in Excel all numbers are doubles
                .expectColumn(1, 1., -20003., 23., 65.)
                .expectColumn(2, 0., 34567890324., -9., 15.)
                .expectColumn(3, -0.1, 8.45, 7.0001, 67.)

                .expectColumn(4, "a", "B", "", null)

                // in Excel dates and date/times are loaded as DateTimes
                .expectColumn(5, 0.001, null, LocalDateTime.of(2000, 3, 4, 14, 2, 3), LocalDateTime.of(2003, 5, 7, 0, 0, 0));
    }

    @Test
    public void testSaveSheet_ToStream() {

        DataFrame df = DataFrame.newFrame("C1", "C2").foldByRow(
                "a", "b",
                "c", "d");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Excel.saver().saveSheet(df, out, "s1");

        DataFrame reload = Excel.loader().firstRowAsHeader().loadSheet(new ByteArrayInputStream(out.toByteArray()), "s1");
        new DataFrameAsserts(reload, "C1", "C2")
                .expectHeight(2)
                .expectRow(0, "a", "b")
                .expectRow(1, "c", "d");
    }

    @Test
    public void testSaveSheet_ToFile() {

        File file = new File(outPath("testSave_ToFile.xlsx"));

        DataFrame df = DataFrame.newFrame("C1", "C2").foldByRow(
                "a", "b",
                "c", "d");

        Excel.saver().saveSheet(df, file, "s1");

        DataFrame reload = Excel.loader().firstRowAsHeader().loadSheet(file, "s1");
        new DataFrameAsserts(reload, "C1", "C2")
                .expectHeight(2)
                .expectRow(0, "a", "b")
                .expectRow(1, "c", "d");
    }

    @Test
    public void testSaveSheet_ToFilePath() {

        String filePath = outPath("testSave_ToFilePath.xlsx");

        DataFrame df = DataFrame.newFrame("C1", "C2").foldByRow(
                "a", "b",
                "c", "d");

        Excel.saver().saveSheet(df, filePath, "s1");

        DataFrame reload = Excel.loader().firstRowAsHeader().loadSheet(filePath, "s1");
        new DataFrameAsserts(reload, "C1", "C2")
                .expectHeight(2)
                .expectRow(0, "a", "b")
                .expectRow(1, "c", "d");
    }

    @Test
    public void testSaveSheet_Mkdirs() {

        String path = outPath("Mkdirs" + File.separator + "f2" + File.separator + "testSave_Mkdirs.xlsx");
        File file = new File(path);

        DataFrame df = DataFrame.newFrame("C1", "C2").foldByRow(
                "a", "b",
                "c", "d");

        Excel.saver().createMissingDirs().saveSheet(df, file, "s1");

        DataFrame reload = Excel.loader().firstRowAsHeader().loadSheet(file, "s1");
        new DataFrameAsserts(reload, "C1", "C2")
                .expectHeight(2)
                .expectRow(0, "a", "b")
                .expectRow(1, "c", "d");
    }

    @Test
    public void testSaveSheet_NoMkdirs() {

        String path = outPath("NoMkdirs" + File.separator + "f4" + File.separator + "testSave_NoMkdirs.xlsx");
        File file = new File(path);

        DataFrame df = DataFrame.newFrame("C1", "C2").foldByRow(
                "a", "b",
                "c", "d");

        assertThrows(RuntimeException.class, () -> Excel.saver().saveSheet(df, file, "s1"));
    }

    @Test
    public void testSaveSheet_NoHeader() {

        File file = new File(outPath("testSave_NoHeader.xlsx"));

        DataFrame df = DataFrame.newFrame("C1", "C2").foldByRow(
                "x", "y",
                "c", "d");

        Excel.saver().noHeader().saveSheet(df, file, "s1");

        assertTrue(file.exists());
        DataFrame reload = Excel.loader().firstRowAsHeader().loadSheet(file, "s1");
        new DataFrameAsserts(reload, "x", "y")
                .expectHeight(1)
                .expectRow(0, "c", "d");
    }

    @Test
    public void testSaveSheet_Merge() {

        File file = new File(outPath("testSave_MultiSheet.xlsx"));

        DataFrame df1 = DataFrame.newFrame("C1", "C2").foldByRow(
                "a", "b",
                "d", "c");

        DataFrame df2 = DataFrame.newFrame("C3", "C4").foldByRow(
                "e", "f",
                "g", "h");

        DataFrame df3 = DataFrame.newFrame("C5", "C6").foldByRow(
                "i", "j",
                "k", "l");

        // New sheets are appended to the existing Excel; existing sheets are overridden
        Excel.saver().saveSheet(df1, file, "s1");
        Excel.saver().saveSheet(df2, file, "s2");
        Excel.saver().saveSheet(df3, file, "s1");

        Map<String, DataFrame> reload = Excel.loader().firstRowAsHeader().load(file);
        assertEquals(2, reload.size());
        assertEquals(Set.of("s1", "s2"), reload.keySet());

        new DataFrameAsserts(reload.get("s1"), "C5", "C6")
                .expectHeight(2)
                .expectRow(0, "i", "j")
                .expectRow(1, "k", "l");

        new DataFrameAsserts(reload.get("s2"), "C3", "C4")
                .expectHeight(2)
                .expectRow(0, "e", "f")
                .expectRow(1, "g", "h");
    }

    @Test
    public void testSaveSheet_CustomStyle() throws IOException {

        DataFrame df = DataFrame.newFrame("C1", "C2").foldByRow(
                "a", "b",
                "c", "d");

        try (Workbook wb = WorkbookFactory.create(true)) {

            Sheet s = wb.createSheet("s1");
            ExcelStyleCustomizer customizer = (wbc, stc) -> {
                Font f = wbc.createFont();
                f.setBold(true);
                stc.setFont(f);
            };

            Excel.saver()
                    .columnStyles(Map.of("C2", customizer))
                    .noHeader()
                    .saveSheet(df, s);

            assertEquals("a", s.getRow(0).getCell(0).getStringCellValue());
            assertEquals("b", s.getRow(0).getCell(1).getStringCellValue());
            assertEquals("c", s.getRow(1).getCell(0).getStringCellValue());
            assertEquals("d", s.getRow(1).getCell(1).getStringCellValue());

            Font f00 = wb.getFontAt(s.getRow(0).getCell(0).getCellStyle().getFontIndex());
            Font f01 = wb.getFontAt(s.getRow(0).getCell(1).getCellStyle().getFontIndex());
            Font f10 = wb.getFontAt(s.getRow(1).getCell(0).getCellStyle().getFontIndex());
            Font f11 = wb.getFontAt(s.getRow(1).getCell(1).getCellStyle().getFontIndex());

            assertFalse(f00.getBold());
            assertTrue(f01.getBold());

            assertFalse(f10.getBold());
            assertTrue(f11.getBold());
        }
    }

    @Test
    public void testSaveSheet_CustomColumnWidth() throws IOException {

        DataFrame df = DataFrame.newFrame("C1", "C2").foldByRow(
                "--", "b",
                "c", "d");

        try (Workbook wb = WorkbookFactory.create(true)) {

            Sheet s = wb.createSheet("s1");
            Excel.saver()
                    .columnWidths(Map.of("C1", 0, "C2", 256 * 5))
                    .noHeader()
                    .saveSheet(df, s);

            // this is an auto-sized column...
            // Its actual width depends on the default font, and is generally platform-specific,
            // so doing a range comparison
            assertTrue(s.getColumnWidth(0) > 600 && s.getColumnWidth(0) < 700);

            assertEquals(256 * 5, s.getColumnWidth(1));
        }
    }

    @Test
    public void testSaveSheet_CustomColumnWidth_AutosizeColumnWidth() throws IOException {

        DataFrame df = DataFrame.newFrame("C1", "C2", "C3").foldByRow(
                "--", "b", "--",
                "c", "d", null);

        try (Workbook wb = WorkbookFactory.create(true)) {

            Sheet s = wb.createSheet("s1");
            Excel.saver()
                    .autoSizeColumns()
                    .columnWidths(Map.of("C1", 0, "C2", 256 * 5))
                    .noHeader()
                    .saveSheet(df, s);

            // this is an auto-sized column...
            // Its actual width depends on the default font, and is generally platform-specific,
            // so doing a range comparison
            assertTrue(s.getColumnWidth(0) > 600 && s.getColumnWidth(0) < 700);
            assertEquals(256 * 5, s.getColumnWidth(1));
            assertEquals(s.getColumnWidth(0), s.getColumnWidth(2), "Auto-sizing should happen for columns not mentioned explicitly");
        }
    }
}
