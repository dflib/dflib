package com.nhl.dflib.excel;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.DoubleSeries;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.LongSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ExcelSaverTest extends BaseExcelTest {

    @Test
    public void testSave_Types() {

        File file = new File(outPath("testSave_Types.xlsx"));

        DataFrame df = DataFrame.newFrame("bool", "int", "long", "double", "mix")
                .columns(
                        BooleanSeries.forBooleans(true, false, false, true),
                        IntSeries.forInts(1, -20003, 23, 65),
                        LongSeries.forLongs(0, 34567890324L, -9L, 15),
                        DoubleSeries.forDoubles(-0.1, 8.45, 7.0001, 67.),
                        Series.forData(new BigDecimal("0.001"), null, LocalDateTime.of(2000, 3, 4, 14, 2, 3), LocalDate.of(2003, 5, 7))
                );

        Excel.saver().save(df, file, "s1");

        DataFrame reload = Excel.loader().firstRowAsHeader().loadSheet(file, "s1");
        new DataFrameAsserts(reload, "bool", "int", "long", "double", "mix")
                .expectHeight(4)
                .expectColumn(0, true, false, false, true)

                // in Excel all numbers are doubles
                .expectColumn(1, 1., -20003., 23., 65.)
                .expectColumn(2, 0., 34567890324., -9., 15.)
                .expectColumn(3, -0.1, 8.45, 7.0001, 67.)

                // in Excel dates and date/times are loaded as DateTimes
                .expectColumn(4, 0.001, null, LocalDateTime.of(2000, 3, 4, 14, 2, 3), LocalDateTime.of(2003, 5, 7, 0, 0, 0));
    }

    @Test
    public void testSave_ToStream() {

        DataFrame df = DataFrame.newFrame("C1", "C2").foldByRow(
                1, 2,
                3, 4);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Excel.saver().save(df, out, "s1");

        DataFrame reload = Excel.loader().firstRowAsHeader().loadSheet(new ByteArrayInputStream(out.toByteArray()), "s1");
        new DataFrameAsserts(reload, "C1", "C2")
                .expectHeight(2)
                .expectRow(0, 1., 2.)
                .expectRow(1, 3., 4.);
    }

    @Test
    public void testSave_ToFile() {

        File file = new File(outPath("testSave_ToFile.xlsx"));

        DataFrame df = DataFrame.newFrame("C1", "C2").foldByRow(
                1, 2,
                3, 4);

        Excel.saver().save(df, file, "s1");

        DataFrame reload = Excel.loader().firstRowAsHeader().loadSheet(file, "s1");
        new DataFrameAsserts(reload, "C1", "C2")
                .expectHeight(2)
                .expectRow(0, 1., 2.)
                .expectRow(1, 3., 4.);
    }

    @Test
    public void testSave_ToFilePath() {

        String filePath = outPath("testSave_ToFilePath.xlsx");

        DataFrame df = DataFrame.newFrame("C1", "C2").foldByRow(
                1, 2,
                3, 4);

        Excel.saver().save(df, filePath, "s1");

        DataFrame reload = Excel.loader().firstRowAsHeader().loadSheet(filePath, "s1");
        new DataFrameAsserts(reload, "C1", "C2")
                .expectHeight(2)
                .expectRow(0, 1., 2.)
                .expectRow(1, 3., 4.);
    }

    @Test
    public void testSave_Mkdirs() {

        String path = outPath("Mkdirs" + File.separator + "f2" + File.separator + "testSave_Mkdirs.xlsx");
        File file = new File(path);

        DataFrame df = DataFrame.newFrame("C1", "C2").foldByRow(
                1, 2,
                3, 4);

        Excel.saver().createMissingDirs().save(df, file, "s1");

        DataFrame reload = Excel.loader().firstRowAsHeader().loadSheet(file, "s1");
        new DataFrameAsserts(reload, "C1", "C2")
                .expectHeight(2)
                .expectRow(0, 1., 2.)
                .expectRow(1, 3., 4.);
    }

    @Test
    public void testSave_NoMkdirs() {

        String path = outPath("NoMkdirs" + File.separator + "f4" + File.separator + "testSave_NoMkdirs.xlsx");
        File file = new File(path);

        DataFrame df = DataFrame.newFrame("C1", "C2").foldByRow(
                1, 2,
                3, 4);

        assertThrows(RuntimeException.class, () -> Excel.saver().save(df, file, "s1"));
    }

    @Test
    public void testSave_NoHeader() {

        File file = new File(outPath("testSave_NoHeader.xlsx"));

        DataFrame df = DataFrame.newFrame("C1", "C2").foldByRow(
                1, 2,
                3, 4);

        Excel.saver().noHeader().save(df, file, "s1");

        assertTrue(file.exists());
        DataFrame reload = Excel.loader().firstRowAsHeader().loadSheet(file, "s1");
        new DataFrameAsserts(reload, "1.0", "2.0")
                .expectHeight(1)
                .expectRow(0, 3., 4.);
    }

    @Test
    public void testSave_MultiSheet() {

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
        Excel.saver().save(df1, file, "s1");
        Excel.saver().save(df2, file, "s2");
        Excel.saver().save(df3, file, "s1");

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
}
