package org.dflib.csv;

import org.dflib.DataFrame;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvSaver_SaveTargetsTest {

    public static final DataFrame DF = DataFrame.foldByRow("A", "B").of(
            1, 2,
            3, 4);

    @TempDir
    Path outBase;

    @Test
    public void saveToString() {
        assertEquals("A,B\r\n1,2\r\n3,4\r\n", Csv.saver().saveToString(DF));
    }

    @Test
    public void save_ToFile() throws IOException {
        Path out = outBase.resolve("save_ToFile.csv");
        Csv.saver().save(DF, out.toFile());
        String csv = Files.readString(out);

        assertEquals("A,B\r\n1,2\r\n3,4\r\n", csv);
    }


    @Test
    public void save_ToFilePath() throws IOException {
        Path out = outBase.resolve("save_ToFilePath.csv");
        Csv.saver().save(DF, out.toFile().getAbsolutePath());
        String csv = Files.readString(out);

        assertEquals("A,B\r\n1,2\r\n3,4\r\n", csv);
    }

    @Test
    public void save_ToPath() throws IOException {
        Path out = outBase.resolve("save_ToPath.csv");
        Csv.saver().save(DF, out);
        String csv = Files.readString(out);

        assertEquals("A,B\r\n1,2\r\n3,4\r\n", csv);
    }

    @Test
    public void save_ToWriter() {
        StringWriter out = new StringWriter();
        Csv.saver().save(DF, out);
        assertEquals("A,B\r\n1,2\r\n3,4\r\n", out.toString());
    }

    @Test
    public void save_ToOutputStream() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Csv.saver().save(DF, out);
        assertEquals("A,B\r\n1,2\r\n3,4\r\n", out.toString());
    }

    @Test
    public void save_ToPath_NoMkdirs() {
        Path out = outBase.resolve("no_such_dir").resolve("save_ToPath_NoMkdirs.csv");
        assertThrows(RuntimeException.class, () -> Csv.saver().save(DF, out));
    }

    @Test
    public void save_ToPath_Mkdirs() throws IOException {
        Path out = outBase.resolve("no_such_dir").resolve("save_ToPath_Mkdirs.csv");
        Csv.saver().createMissingDirs().save(DF, out);
        String csv = Files.readString(out);

        assertEquals("A,B\r\n1,2\r\n3,4\r\n", csv);
    }
}
