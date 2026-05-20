package org.dflib.csv;

import org.dflib.DataFrame;
import org.dflib.codec.Codec;
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

    private static final DataFrame DF = DataFrame.foldByRow("A", "B").of(
            1, 2,
            3, 4);

    @TempDir
    Path outBase;

    @Test
    public void save_ToFile() throws IOException {
        Path out = outBase.resolve("save_ToFile");
        Csv.saver().compression(Codec.GZIP).save(DF, out.toFile());
        String csv = readAndUncompress(out);

        assertEquals("A,B\r\n1,2\r\n3,4\r\n", csv);
    }

    @ParameterizedTest
    @ValueSource(strings = {"save_ToFile.csv.gz", "save_ToFile.csv.gzip"})
    public void save_ToFile_CodecByExtension(String fileName) throws IOException {
        Path out = outBase.resolve(fileName);
        Csv.saver().save(DF, out.toFile());
        String csv = readAndUncompress(out);

        assertEquals("A,B\r\n1,2\r\n3,4\r\n", csv);
    }


    @Test
    public void save_ToFilePath() throws IOException {
        Path out = outBase.resolve("save_ToFilePath.csv");
        Csv.saver().compression(Codec.GZIP).save(DF, out.toFile().getAbsolutePath());
        String csv = readAndUncompress(out);

        assertEquals("A,B\r\n1,2\r\n3,4\r\n", csv);
    }

    @Test
    public void save_ToPath() throws IOException {
        Path out = outBase.resolve("save_ToPath.csv");
        Csv.saver().compression(Codec.GZIP).save(DF, out);
        String csv = readAndUncompress(out);

        assertEquals("A,B\r\n1,2\r\n3,4\r\n", csv);
    }

    @ParameterizedTest
    @ValueSource(strings = {"save_ToPath.csv.gz", "save_ToPath.csv.gzip"})
    public void save_ToPath_CompressByExtension(String filePath) throws IOException {
        Path out = outBase.resolve(filePath);
        Csv.saver().save(DF, out);
        String csv = readAndUncompress(out);

        assertEquals("A,B\r\n1,2\r\n3,4\r\n", csv);
    }

    @Test
    public void save_ToOutputStream() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Csv.saver().compression(Codec.GZIP).save(DF, out);
        String csv = readAndUncompress(out.toByteArray());

        assertEquals("A,B\r\n1,2\r\n3,4\r\n", csv);
    }

    @Test
    public void save_WithCodecFromFileName_ThenWithoutCodec() throws IOException {
        CsvSaver saver = Csv.saver();

        Path gzipFile = outBase.resolve("file1.csv.gz");
        saver.save(DF, gzipFile.toFile());

        Path plainFile = outBase.resolve("file2.csv");
        saver.save(DF, plainFile.toFile());

        byte[] gzipBytes = Files.readAllBytes(gzipFile);
        assertEquals((byte) 0x1f, gzipBytes[0], "First file should be GZIP compressed");
        assertEquals((byte) 0x8b, gzipBytes[1], "First file should be GZIP compressed");

        String actual = Files.readString(plainFile);
        assertEquals("A,B\r\n1,2\r\n3,4\r\n", actual, "Second file should not be compressed");
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
