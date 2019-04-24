package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import org.apache.commons.csv.CSVFormat;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.*;

public class CsvSaverTest extends BaseCsvTest {

    @Test
    public void testSaveToString() {

        DataFrame df = DataFrame.forSequenceFoldByRow(Index.forLabels("A", "B"),
                1, 2,
                3, 4);

        assertEquals("A,B\r\n" +
                "1,2\r\n" +
                "3,4\r\n", Csv.saver().saveToString(df));
    }

    @Test
    public void testSave_ToWriter() {

        DataFrame df = DataFrame.forSequenceFoldByRow(Index.forLabels("A", "B"),
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

        DataFrame df = DataFrame.forSequenceFoldByRow(Index.forLabels("A", "B"),
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

        DataFrame df = DataFrame.forSequenceFoldByRow(Index.forLabels("A", "B"),
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

        DataFrame df = DataFrame.forSequenceFoldByRow(Index.forLabels("A", "B"),
                1, 2,
                3, 4);

        Csv.saver().save(df, filePath);
        assertEquals("A,B\r\n" +
                "1,2\r\n" +
                "3,4\r\n", readFile(filePath));
    }
}
