package org.dflib.excel;

import org.dflib.DataFrame;
import org.dflib.ByteSource;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExcelLoader_LoadSourcesTest {

    @ParameterizedTest
    @ValueSource(strings = {"one-sheet.xls", "one-sheet.xlsx"})
    public void fromFile(String source) throws URISyntaxException {

        File file = new File(getClass().getResource(source).toURI());

        Map<String, DataFrame> data = new ExcelLoader().load(file);
        assertEquals(1, data.size());
        DataFrame df = data.get("Sheet1");
        assertNotNull(df);

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "One", "Two")
                .expectRow(1, "Three", "Four");
    }

    @ParameterizedTest
    @ValueSource(strings = {"one-sheet.xls", "one-sheet.xlsx"})
    public void fromStringFilePath(String source) throws URISyntaxException {

        File file = new File(getClass().getResource(source).toURI());

        Map<String, DataFrame> data = new ExcelLoader().load(file.getPath());
        assertEquals(1, data.size());
        DataFrame df = data.get("Sheet1");
        assertNotNull(df);

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "One", "Two")
                .expectRow(1, "Three", "Four");
    }

    @ParameterizedTest
    @ValueSource(strings = {"one-sheet.xls", "one-sheet.xlsx"})
    public void fromPath(String source) throws URISyntaxException {

        Path path = Paths.get(getClass().getResource(source).toURI());

        Map<String, DataFrame> data = new ExcelLoader().load(path);
        assertEquals(1, data.size());
        DataFrame df = data.get("Sheet1");
        assertNotNull(df);

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "One", "Two")
                .expectRow(1, "Three", "Four");
    }

    @ParameterizedTest
    @ValueSource(strings = {"one-sheet.xls", "one-sheet.xlsx"})
    public void fromStream(String source) throws IOException {

        try (InputStream in = getClass().getResourceAsStream(source)) {

            Map<String, DataFrame> data = new ExcelLoader().load(in);
            assertEquals(1, data.size());
            DataFrame df = data.get("Sheet1");
            assertNotNull(df);

            new DataFrameAsserts(df, "A", "B")
                    .expectHeight(2)
                    .expectRow(0, "One", "Two")
                    .expectRow(1, "Three", "Four");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"multi-sheet.xls", "multi-sheet.xlsx"})
    public void fromStreamMultiSheet(String source) throws IOException {

        try (InputStream in = getClass().getResourceAsStream(source)) {

            Map<String, DataFrame> data = new ExcelLoader().load(in);

            // the ordering of keys in the map must match the order in Excel
            List<String> keys = data.keySet().stream().collect(Collectors.toList());
            assertEquals(asList("S2", "S0", "Some Sheet", "S1"), keys);

            new DataFrameAsserts(data.get("S1"), "A", "B")
                    .expectHeight(2)
                    .expectRow(0, "One", "Two")
                    .expectRow(1, "Three", "Four");

            new DataFrameAsserts(data.get("S2"), "A", "B", "C", "D")
                    .expectHeight(1)
                    .expectRow(0, "Five", "Six", "Seven", "Eight");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"one-sheet.xls", "one-sheet.xlsx"})
    public void fromByteSource(String source) {

        Map<String, DataFrame> data = new ExcelLoader().load(ByteSource.ofUrl(getClass().getResource(source)));
        assertEquals(1, data.size());
        DataFrame df = data.get("Sheet1");
        assertNotNull(df);

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "One", "Two")
                .expectRow(1, "Three", "Four");
    }
}
