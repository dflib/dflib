package org.dflib.json;

import org.dflib.ByteSource;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class JsonTest {

    @Test
    public void load_fromString() {
        String json = "[1,2,3]";
        DataFrame df = Json.load(json);
        new DataFrameAsserts(df, "_val")
                .expectHeight(3)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 3);
    }

    @Test
    public void load_fromFile() throws URISyntaxException {
        File json = new File(getClass().getResource("list-of-values.json").toURI());
        assertTrue(json.exists());

        DataFrame df = Json.load(json);
        new DataFrameAsserts(df, "_val")
                .expectHeight(3)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 3);
    }

    @Test
    public void load_fromReader() throws URISyntaxException, IOException {
        File json = new File(getClass().getResource("list-of-values.json").toURI());

        try (Reader r = new FileReader(json, StandardCharsets.UTF_8)) {
            DataFrame df = Json.load(r);
            new DataFrameAsserts(df, "_val")
                    .expectHeight(3)
                    .expectRow(0, 1)
                    .expectRow(1, 2)
                    .expectRow(2, 3);
        }
    }

    @Test
    public void load_fromPath() throws URISyntaxException {
        Path json = new File(getClass().getResource("list-of-values.json").toURI()).toPath();

        DataFrame df = Json.load(json);
        new DataFrameAsserts(df, "_val")
                .expectHeight(3)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 3);
    }

    @Test
    public void load_fromInputStream() throws URISyntaxException, IOException {
        File json = new File(getClass().getResource("list-of-values.json").toURI());

        try (InputStream in = new FileInputStream(json)) {
            DataFrame df = Json.load(in);
            new DataFrameAsserts(df, "_val")
                    .expectHeight(3)
                    .expectRow(0, 1)
                    .expectRow(1, 2)
                    .expectRow(2, 3);
        }
    }

    @Test
    public void load_fromByteSource() {

        DataFrame df = Json.load(ByteSource.ofUrl(getClass().getResource("list-of-values.json")));
        new DataFrameAsserts(df, "_val")
                .expectHeight(3)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 3);
    }
}
