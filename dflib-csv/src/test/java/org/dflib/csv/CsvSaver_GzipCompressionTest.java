package org.dflib.csv;

import org.dflib.DataFrame;
import org.dflib.codec.Codec;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.LineBreak;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvSaver_GzipCompressionTest {

    private static final CsvFormat FORMAT = CsvFormat.defaultFormat().lineBreak(LineBreak.LF).build();

    @TempDir
    Path outBase;

    @Test
    public void save_ToFile() throws IOException {
        DataFrame df = DataFrame.foldByRow("A", "B").of(
                1, 2,
                3, 4);

        Path out = outBase.resolve("save_ToFile");
        Csv.saver().format(FORMAT).compression(Codec.GZIP).save(df, out.toFile());
        String csv = readAndUncompress(out);

        assertEquals("A,B\n1,2\n3,4\n", csv);
    }

    @ParameterizedTest
    @ValueSource(strings = {"save_ToFile.csv.gz", "save_ToFile.csv.gzip"})
    public void save_ToFile_CodecByExtension(String fileName) throws IOException {
        DataFrame df = DataFrame.foldByRow("A", "B").of(
                1, 2,
                3, 4);

        Path out = outBase.resolve(fileName);
        Csv.saver().format(FORMAT).save(df, out.toFile());
        String csv = readAndUncompress(out);

        assertEquals("A,B\n1,2\n3,4\n", csv);
    }


    @Test
    public void save_ToFilePath() throws IOException {
        DataFrame df = DataFrame.foldByRow("A", "B").of(
                1, 2,
                3, 4);

        Path out = outBase.resolve("save_ToFilePath.csv");
        Csv.saver().format(FORMAT).compression(Codec.GZIP).save(df, out.toFile().getAbsolutePath());
        String csv = readAndUncompress(out);

        assertEquals("A,B\n1,2\n3,4\n", csv);
    }

    @Test
    public void save_ToPath() throws IOException {
        DataFrame df = DataFrame.foldByRow("A", "B").of(
                1, 2,
                3, 4);

        Path out = outBase.resolve("save_ToPath.csv");
        Csv.saver().format(FORMAT).compression(Codec.GZIP).save(df, out);
        String csv = readAndUncompress(out);

        assertEquals("A,B\n1,2\n3,4\n", csv);
    }

    @ParameterizedTest
    @ValueSource(strings = {"save_ToPath.csv.gz", "save_ToPath.csv.gzip"})
    public void save_ToPath_CompressByExtension(String filePath) throws IOException {
        DataFrame df = DataFrame.foldByRow("A", "B").of(
                1, 2,
                3, 4);

        Path out = outBase.resolve(filePath);
        Csv.saver().format(FORMAT).save(df, out);
        String csv = readAndUncompress(out);

        assertEquals("A,B\n1,2\n3,4\n", csv);
    }

    @Test
    public void save_ToOutputStream() throws IOException {
        DataFrame df = DataFrame.foldByRow("A", "B").of(
                1, 2,
                3, 4);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Csv.saver().format(FORMAT).compression(Codec.GZIP).save(df, out);
        String csv = readAndUncompress(out.toByteArray());

        assertEquals("A,B\n1,2\n3,4\n", csv);
    }

    private static String readAndUncompress(Path path) throws IOException {
        byte[] bytes = Files.readAllBytes(path);
        return readAndUncompress(bytes);
    }

    private static String readAndUncompress(byte[] bytes) throws IOException {
        StringBuilder buf = new StringBuilder();

        try (Reader r = new InputStreamReader(new GZIPInputStream(new ByteArrayInputStream(bytes)))) {
            char[] chars = new char[2048];
            int read;
            while ((read = r.read(chars, 0, chars.length)) != -1) {
                buf.append(chars, 0, read);
            }
        }

        return buf.toString();
    }
}
