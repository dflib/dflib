package org.dflib.excel;

import org.dflib.ByteSource;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class Excel_LoadTest {

    @Test
    public void noModification() throws URISyntaxException, NoSuchAlgorithmException {
        File file = new File(getClass().getResource("one-sheet.xlsx").toURI());

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        Supplier<byte[]> fileHash = () -> {
            digest.reset();
            try (InputStream inputStream = Files.newInputStream(file.toPath())) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    digest.update(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return digest.digest();
        };

        byte[] hashBefore = fileHash.get();
        Excel.load(file);
        byte[] hashAfter = fileHash.get();
        assertArrayEquals(hashBefore, hashAfter, "Loading a file caused a change to its contents");
    }

    @Test
    public void fromFile() throws URISyntaxException {
        File file = new File(getClass().getResource("one-sheet.xlsx").toURI());

        Map<String, DataFrame> data = Excel.load(file);
        assertEquals(1, data.size());
        DataFrame df = data.get("Sheet1");
        assertNotNull(df);

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "One", "Two")
                .expectRow(1, "Three", "Four");
    }

    @Test
    public void fromStringFilePath() throws URISyntaxException {
        File file = new File(getClass().getResource("one-sheet.xlsx").toURI());

        Map<String, DataFrame> data = Excel.load(file.getPath());
        assertEquals(1, data.size());
        DataFrame df = data.get("Sheet1");
        assertNotNull(df);

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "One", "Two")
                .expectRow(1, "Three", "Four");
    }

    @Test
    public void fromPath() throws URISyntaxException {
        Path path = Paths.get(getClass().getResource("one-sheet.xlsx").toURI());

        Map<String, DataFrame> data = Excel.load(path);
        assertEquals(1, data.size());
        DataFrame df = data.get("Sheet1");
        assertNotNull(df);

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "One", "Two")
                .expectRow(1, "Three", "Four");
    }

    @Test
    public void fromStream() throws IOException {

        try (InputStream in = getClass().getResourceAsStream("one-sheet.xlsx")) {

            Map<String, DataFrame> data = Excel.load(in);
            assertEquals(1, data.size());
            DataFrame df = data.get("Sheet1");
            assertNotNull(df);

            new DataFrameAsserts(df, "A", "B")
                    .expectHeight(2)
                    .expectRow(0, "One", "Two")
                    .expectRow(1, "Three", "Four");
        }
    }

    @Test
    public void fromByteSource() {

        Map<String, DataFrame> data = Excel.load(ByteSource.ofUrl(getClass().getResource("one-sheet.xlsx")));
        assertEquals(1, data.size());
        DataFrame df = data.get("Sheet1");
        assertNotNull(df);

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "One", "Two")
                .expectRow(1, "Three", "Four");
    }
}
