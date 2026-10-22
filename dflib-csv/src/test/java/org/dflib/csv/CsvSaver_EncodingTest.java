package org.dflib.csv;

import org.dflib.ByteSource;
import org.dflib.DataFrame;
import org.dflib.junit.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvSaver_EncodingTest {

    public static final DataFrame DF = DataFrame.foldByRow("A", "B").of(
            "ж", "é",
            "x", "y");

    private static final String CSV = "A,B\r\nж,é\r\nx,y\r\n";

    @TempDir
    Path outBase;

    @Test
    public void save_ToOutputStream_Charset() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Csv.saver().encoding(StandardCharsets.UTF_16BE).save(DF, out);
        assertEquals(CSV, out.toString(StandardCharsets.UTF_16BE));
    }

    @Test
    public void save_ToOutputStream_String() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Csv.saver().encoding("UTF-16BE").save(DF, out);
        assertEquals(CSV, out.toString(StandardCharsets.UTF_16BE));
    }

    @Test
    public void save_ToFile() throws IOException {
        Path out = outBase.resolve("save_ToFile.csv");
        Csv.saver().encoding(StandardCharsets.UTF_16LE).save(DF, out);
        assertEquals(CSV, Files.readString(out, StandardCharsets.UTF_16LE));
    }

    @Test
    public void save_NullResetsToDefault() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Csv.saver().encoding(StandardCharsets.UTF_16BE).encoding((Charset) null).save(DF, out);
        assertEquals(CSV, out.toString(Charset.defaultCharset()));
    }

    @Test
    public void save_ToWriter_IgnoresEncoding() {
        StringWriter out = new StringWriter();
        Csv.saver().encoding(StandardCharsets.UTF_16BE).save(DF, out);
        assertEquals(CSV, out.toString());
    }

    @Test
    public void roundTrip() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Csv.saver().encoding(StandardCharsets.UTF_16BE).save(DF, out);

        DataFrame loaded = Csv.loader().encoding(StandardCharsets.UTF_16BE).load(ByteSource.of(out.toByteArray()));
        new DataFrameAsserts(loaded, "A", "B")
                .expectHeight(2)
                .expectRow(0, "ж", "é")
                .expectRow(1, "x", "y");
    }
}
