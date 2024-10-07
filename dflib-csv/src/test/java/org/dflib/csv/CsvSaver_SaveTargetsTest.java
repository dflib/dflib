package org.dflib.csv;

import org.dflib.DataFrame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvSaver_SaveTargetsTest {

    @TempDir
    Path outBase;

    @Test
    public void saveToString() {

        DataFrame df = DataFrame.foldByRow("A", "B").of(
                1, 2,
                3, 4);

        assertEquals("A,B\r\n" +
                "1,2\r\n" +
                "3,4\r\n", Csv.saver().saveToString(df));
    }

    @Test
    public void save_ToFile() throws IOException {

        DataFrame df = DataFrame.foldByRow("A", "B").of(
                1, 2,
                3, 4);

        Path out = outBase.resolve("save_ToFile.csv");
        Csv.saver().save(df, out.toFile());
        String csv = Files.readString(out);

        assertEquals("A,B\r\n" +
                "1,2\r\n" +
                "3,4\r\n", csv);
    }


    @Test
    public void save_ToFilePath() throws IOException {

        DataFrame df = DataFrame.foldByRow("A", "B").of(
                1, 2,
                3, 4);

        Path out = outBase.resolve("save_ToFilePath.csv");
        Csv.saver().save(df, out.toFile().getAbsolutePath());
        String csv = Files.readString(out);

        assertEquals("A,B\r\n" +
                "1,2\r\n" +
                "3,4\r\n", csv);
    }

    @Test
    public void save_ToPath() throws IOException {

        DataFrame df = DataFrame.foldByRow("A", "B").of(
                1, 2,
                3, 4);

        Path out = outBase.resolve("save_ToPath.csv");
        Csv.saver().save(df, out);
        String csv = Files.readString(out);

        assertEquals("A,B\r\n" +
                "1,2\r\n" +
                "3,4\r\n", csv);
    }

    @Test
    public void save_ToWriter() {

        DataFrame df = DataFrame.foldByRow("A", "B").of(
                1, 2,
                3, 4);

        StringWriter out = new StringWriter();
        Csv.saver().save(df, out);
        assertEquals("A,B\r\n" +
                "1,2\r\n" +
                "3,4\r\n", out.toString());
    }

    @Test
    public void save_ToPath_NoMkdirs() {

        DataFrame df = DataFrame.foldByRow("A", "B").of(
                1, 2,
                3, 4);

        Path out = outBase.resolve("no_such_dir").resolve("save_ToPath_NoMkdirs.csv");
        assertThrows(RuntimeException.class, () -> Csv.saver().save(df, out));
    }

    @Test
    public void save_ToPath_Mkdirs() throws IOException {

        DataFrame df = DataFrame.foldByRow("A", "B").of(
                1, 2,
                3, 4);

        Path out = outBase.resolve("no_such_dir").resolve("save_ToPath_Mkdirs.csv");
        Csv.saver().createMissingDirs().save(df, out);
        String csv = Files.readString(out);

        assertEquals("A,B\r\n" +
                "1,2\r\n" +
                "3,4\r\n", csv);
    }
}
