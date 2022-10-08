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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
