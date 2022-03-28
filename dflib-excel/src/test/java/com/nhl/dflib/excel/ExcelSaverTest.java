package com.nhl.dflib.excel;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExcelSaverTest extends BaseExcelTest {


    @Test
    public void testSave_ToFile() throws IOException {

        File file = new File(outPath("testToFile.xls"));

        DataFrame df = DataFrame.newFrame("A", "B").foldByRow(
                1, 2,
                3, 4);

        Excel.saver().save("testToFile", df, file);
        DataFrame expect = Excel.loader().skipHeader().loadSheet(file, "testToFile");

        new DataFrameAsserts(expect, "A", "B")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    public void testSave_ToFileWithMultiDF() throws IOException {

        File file = new File(outPath("testToFile.xls"));

        DataFrame df1 = DataFrame.newFrame("A", "B").foldByRow(
                1, 2,
                3, 4);

        DataFrame df2 = DataFrame.newFrame("C", "D").foldByRow(
                5, 6,
                7, 8);


        Map<String, DataFrame> sheetNameToDF = new HashMap<>();
        sheetNameToDF.put("sheet1", df1);
        sheetNameToDF.put("sheet2", df2);

        Excel.saver().save(sheetNameToDF, file);
        DataFrame expect1 = Excel.loader().skipHeader().loadSheet(file, "sheet1");
        DataFrame expect2 = Excel.loader().skipHeader().loadSheet(file, "sheet2");

        new DataFrameAsserts(expect1, "A", "B")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");

        new DataFrameAsserts(expect2, "A", "B")
                .expectHeight(2)
                .expectRow(0, "5", "6")
                .expectRow(1, "7", "8");
    }

    @Test
    public void testSave_ToFilePath() throws IOException {

        String filePath = outPath("testToFilePath.xls");

        DataFrame df = DataFrame.newFrame("A", "B").foldByRow(
                1, 2,
                3, 4);

        Excel.saver().save("testToFile", df, filePath);
        DataFrame expect = Excel.loader().skipHeader().loadSheet(filePath, "testToFile");

        new DataFrameAsserts(expect, "A", "B")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    public void testSave_Mkdirs() throws IOException {

        String path = outPath("Mkdirs" + File.separator + "f2" + File.separator + "testToFile.xls");
        File file = new File(path);

        DataFrame df = DataFrame.newFrame("A", "B").foldByRow(
                1, 2,
                3, 4);

        Excel.saver().createMissingDirs().save("test", df, file);
        DataFrame expect = Excel.loader().skipHeader().loadSheet(file, "test");

        new DataFrameAsserts(expect, "A", "B")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    public void testSave_NoMkdirs() {

        String path = outPath("NoMkdirs" + File.separator + "f4" + File.separator + "testToFile.xls");
        File file = new File(path);

        DataFrame df = DataFrame.newFrame("A", "B").foldByRow(
                1, 2,
                3, 4);

        assertThrows(RuntimeException.class, () -> Excel.saver().noHeader().save("test", df, file));
    }


    @Test
    public void testSave_noHeader() {
        String path = outPath("testToFile.xls");
        File file = new File(path);

        DataFrame df = DataFrame.newFrame("A", "B").foldByRow(
                1, 2,
                3, 4);

        Excel.saver().noHeader().save("test", df, file);

        DataFrame expect = Excel.loader().loadSheet(file, "test");
        new DataFrameAsserts(expect, "A", "B")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }
}
