package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;
import org.apache.commons.csv.CSVFormat;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

public class CsvSaverTest extends BaseCsvTest {

    @Test
    public void testSaveToString() {

        DataFrame df = DataFrame.newFrame("A", "B").foldByRow(
                1, 2,
                3, 4);

        assertEquals("A,B\r\n" +
                "1,2\r\n" +
                "3,4\r\n", Csv.saver().saveToString(df));
    }

    @Test
    public void testSave_ToWriter() {

        DataFrame df = DataFrame.newFrame("A", "B").foldByRow(
                1, 2,
                3, 4);

        StringWriter out = new StringWriter();
        Csv.saver().save(df, out);
        assertEquals("A,B\r\n" +
                "1,2\r\n" +
                "3,4\r\n", out.toString());
    }

    @Test
    public void testSave_ToWriter_Format() {

        DataFrame df = DataFrame.newFrame("A", "B").foldByRow(
                1, 2,
                3, 4);

        StringWriter out = new StringWriter();
        Csv.saver().format(CSVFormat.MYSQL).save(df, out);

        assertEquals("A\tB\n" +
                "1\t2\n" +
                "3\t4\n", out.toString());
    }

    @Test
    public void testSave_ToFile() throws IOException {

        File file = new File(outPath("testToFile.csv"));

        DataFrame df = DataFrame.newFrame("A", "B").foldByRow(
                1, 2,
                3, 4);

        Csv.saver().save(df, file);
        assertEquals("A,B\r\n" +
                "1,2\r\n" +
                "3,4\r\n", readFile(file.getAbsolutePath()));
    }

    @Test
    public void testSave_ToFilePath() throws IOException {

        String filePath = outPath("testToFilePath.csv");

        DataFrame df = DataFrame.newFrame("A", "B").foldByRow(
                1, 2,
                3, 4);

        Csv.saver().save(df, filePath);
        assertEquals("A,B\r\n" +
                "1,2\r\n" +
                "3,4\r\n", readFile(filePath));
    }

    @Test
    public void testSave_Mkdirs() throws IOException {

        String path = outPath("Mkdirs" + File.separator + "f2" + File.separator + "testToFile.csv");
        File file = new File(path);

        DataFrame df = DataFrame.newFrame("A", "B").foldByRow(
                1, 2,
                3, 4);

        Csv.saver().createMissingDirs().save(df, file);
        assertEquals("A,B\r\n" +
                "1,2\r\n" +
                "3,4\r\n", readFile(file.getAbsolutePath()));
    }

    @Test(expected = RuntimeException.class)
    public void testSave_NoMkdirs() {

        String path = outPath("NoMkdirs" + File.separator + "f4" + File.separator + "testToFile.csv");
        File file = new File(path);

        DataFrame df = DataFrame.newFrame("A", "B").foldByRow(
                1, 2,
                3, 4);

        Csv.saver().save(df, file);
    }


    @Test
    public void testSave_NoHeader() {
        DataFrame df = DataFrame.newFrame("A", "B").foldByRow(
                1, 2,
                3, 4);

        assertEquals(
                "1,2\r\n" +
                        "3,4\r\n", Csv.saver().noHeader().saveToString(df));
    }
}
