package com.nhl.dflib.excel;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class ExcelLoaderTest {

    @ParameterizedTest
    @ValueSource(strings = {"one-sheet.xls", "one-sheet.xlsx"})
    public void testFromStream(String source) throws IOException {

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
    public void testFromStreamMultiSheet(String source) throws IOException {

        try (InputStream in = getClass().getResourceAsStream(source)) {

            Map<String, DataFrame> data = new ExcelLoader().load(in);
            assertEquals(new HashSet<>(asList("S1", "S2")), data.keySet());

            new DataFrameAsserts(data.get("S1"), "A", "B")
                    .expectHeight(2)
                    .expectRow(0, "One", "Two")
                    .expectRow(1, "Three", "Four");

            new DataFrameAsserts(data.get("S2"), "A", "B", "C", "D")
                    .expectHeight(1)
                    .expectRow(0, "Five", "Six", "Seven", "Eight");
        }
    }

    @Test
    public void testLoad_AsymmetricColumns() throws IOException {

        try (InputStream in = getClass().getResourceAsStream("one-sheet-asymmetric-columns.xlsx")) {
            DataFrame df = new ExcelLoader().load(in).get("Sheet1");
            assertArrayEquals(
                    new String[]{"B", "C", "D", "E", "F", "G", "H", "I", "J"},
                    df.getColumnsIndex().getLabels());
        }
    }
}
