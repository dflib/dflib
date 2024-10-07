package org.dflib.excel;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExcelSaver_SaveSheetTest {

    @Test
    public void saveSheet_Types() {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        DataFrame df = DataFrame.byColumn("bool", "int", "long", "double", "string", "mix")
                .of(
                        Series.ofBool(true, false, false, true),
                        Series.ofInt(1, -20003, 23, 65),
                        Series.ofLong(0, 34567890324L, -9L, 15),
                        Series.ofDouble(-0.1, 8.45, 7.0001, 67.),
                        Series.of("a", "B", "", null),
                        Series.of(new BigDecimal("0.001"), null, LocalDateTime.of(2000, 3, 4, 14, 2, 3), LocalDate.of(2003, 5, 7))
                );

        Excel.saver().saveSheet(df, out, "s1");

        DataFrame reload = Excel.loader().firstRowAsHeader().loadSheet(new ByteArrayInputStream(out.toByteArray()), "s1");
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
    public void saveSheet_NoHeader() {

        DataFrame df = DataFrame.foldByRow("C1", "C2").of(
                "x", "y",
                "c", "d");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Excel.saver().noHeader().saveSheet(df, out, "s1");

        assertTrue(out.size() > 0);
        DataFrame reload = Excel.loader().firstRowAsHeader().loadSheet(new ByteArrayInputStream(out.toByteArray()), "s1");
        new DataFrameAsserts(reload, "x", "y")
                .expectHeight(1)
                .expectRow(0, "c", "d");
    }

    @Test
    public void saveSheet_CustomStyle() throws IOException {

        DataFrame df = DataFrame.foldByRow("C1", "C2").of(
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
    public void saveSheet_CustomColumnWidth() throws IOException {

        DataFrame df = DataFrame.foldByRow("C1", "C2").of(
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
            assertTrue(s.getColumnWidth(0) > 500 && s.getColumnWidth(0) < 700, () -> "Actual column width: " + s.getColumnWidth(0));

            assertEquals(256 * 5, s.getColumnWidth(1));
        }
    }

    @Test
    public void saveSheet_CustomColumnWidth_AutosizeColumnWidth() throws IOException {

        DataFrame df = DataFrame.foldByRow("C1", "C2", "C3").of(
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
            assertTrue(s.getColumnWidth(0) > 500 && s.getColumnWidth(0) < 700, () -> "Actual column width: " + s.getColumnWidth(0));
            assertEquals(256 * 5, s.getColumnWidth(1));
            assertEquals(s.getColumnWidth(0), s.getColumnWidth(2), "Auto-sizing should happen for columns not mentioned explicitly");
        }
    }
}
