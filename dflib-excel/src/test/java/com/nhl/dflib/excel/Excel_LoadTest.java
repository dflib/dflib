package com.nhl.dflib.excel;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Excel_LoadTest {

    @Test
    public void testFromFile() throws URISyntaxException {
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
    public void testFromStringFilePath() throws URISyntaxException {
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
    public void testFromPath() throws URISyntaxException {
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
    public void testFromStream() throws IOException {

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
}
