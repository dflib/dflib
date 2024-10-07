package org.dflib.json;

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

public class JsonLoader_LoadSourcesTest {

    @Test
    public void fromString() {
        String json = "[1,2,3]";
        DataFrame df = new JsonLoader().load(json);
        new DataFrameAsserts(df, "_val")
                .expectHeight(3)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 3);
    }

    @Test
    public void fromFile() throws URISyntaxException {
        File json = new File(getClass().getResource("list-of-values.json").toURI());
        assertTrue(json.exists());

        DataFrame df = new JsonLoader().load(json);
        new DataFrameAsserts(df, "_val")
                .expectHeight(3)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 3);
    }

    @Test
    public void fromReader() throws URISyntaxException, IOException {
        File json = new File(getClass().getResource("list-of-values.json").toURI());

        try (Reader r = new FileReader(json, StandardCharsets.UTF_8)) {
            DataFrame df = new JsonLoader().load(r);
            new DataFrameAsserts(df, "_val")
                    .expectHeight(3)
                    .expectRow(0, 1)
                    .expectRow(1, 2)
                    .expectRow(2, 3);
        }
    }

    @Test
    public void fromPath() throws URISyntaxException {
        Path json = new File(getClass().getResource("list-of-values.json").toURI()).toPath();

        DataFrame df = new JsonLoader().load(json);
        new DataFrameAsserts(df, "_val")
                .expectHeight(3)
                .expectRow(0, 1)
                .expectRow(1, 2)
                .expectRow(2, 3);
    }

    @Test
    public void fromInputStream() throws URISyntaxException, IOException {
        File json = new File(getClass().getResource("list-of-values.json").toURI());

        try (InputStream in = new FileInputStream(json)) {
            DataFrame df = new JsonLoader().load(in);
            new DataFrameAsserts(df, "_val")
                    .expectHeight(3)
                    .expectRow(0, 1)
                    .expectRow(1, 2)
                    .expectRow(2, 3);
        }
    }
}
