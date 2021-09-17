package com.nhl.dflib.excel;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExcelLoader_LoadSheetTest {

    @ParameterizedTest
    @ValueSource(strings = {"multi-sheet.xls", "multi-sheet.xlsx"})
    public void testFromStream_MultiSheet(String source) throws IOException {

        try (InputStream in = getClass().getResourceAsStream(source)) {

            DataFrame s1 = new ExcelLoader().loadSheet(in, "S1");
            new DataFrameAsserts(s1, "A", "B")
                    .expectHeight(2)
                    .expectRow(0, "One", "Two")
                    .expectRow(1, "Three", "Four");
        }

        // annoying - POI closes InputStream that it didn't open... So have to restart the stream to load another Sheet

        try (InputStream in = getClass().getResourceAsStream(source)) {

            DataFrame s2 = new ExcelLoader().loadSheet(in, "S2");
            new DataFrameAsserts(s2, "A", "B", "C", "D")
                    .expectHeight(1)
                    .expectRow(0, "Five", "Six", "Seven", "Eight");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"multi-sheet.xls", "multi-sheet.xlsx"})
    public void testFromFile_MultiSheet(String source) throws URISyntaxException {

        File file = new File(getClass().getResource(source).toURI());

        DataFrame s1 = new ExcelLoader().loadSheet(file, "S1");
        new DataFrameAsserts(s1, "A", "B")
                .expectHeight(2)
                .expectRow(0, "One", "Two")
                .expectRow(1, "Three", "Four");

        DataFrame s2 = new ExcelLoader().loadSheet(file, "S2");
        new DataFrameAsserts(s2, "A", "B", "C", "D")
                .expectHeight(1)
                .expectRow(0, "Five", "Six", "Seven", "Eight");
    }

    @ParameterizedTest
    @ValueSource(strings = {"multi-sheet.xls", "multi-sheet.xlsx"})
    public void testFromFile_MultiSheet_ByPosition(String source) throws URISyntaxException {

        File file = new File(getClass().getResource(source).toURI());

        DataFrame s1 = new ExcelLoader().loadSheet(file, 3);
        new DataFrameAsserts(s1, "A", "B")
                .expectHeight(2)
                .expectRow(0, "One", "Two")
                .expectRow(1, "Three", "Four");

        DataFrame s2 = new ExcelLoader().loadSheet(file, 0);
        new DataFrameAsserts(s2, "A", "B", "C", "D")
                .expectHeight(1)
                .expectRow(0, "Five", "Six", "Seven", "Eight");
    }

    @Test
    public void testInvalidSheetName() throws URISyntaxException {
        File file = new File(getClass().getResource("multi-sheet.xlsx").toURI());
        assertThrows(RuntimeException.class, () -> new ExcelLoader().loadSheet(file, "No_such_Sheet"));
    }

    @Test
    public void testDataTypes() throws URISyntaxException {

        File file = new File(getClass().getResource("one-sheet-data-types.xlsx").toURI());

        DataFrame df = new ExcelLoader().loadSheet(file, "Sheet1");
        new DataFrameAsserts(df, "A", "B")
                .expectHeight(9)
                .expectRow(0, "Boolean", true)
                // TODO: can we guess that an int is an int, and not a double?
                .expectRow(1, "Int", 1d)
                // TODO: can we guess that a long is a long, and not a double?
                .expectRow(2, "Long", 2147483647123d)
                .expectRow(3, "Double", 1.11)
                .expectRow(4, "Negative Double", -1.111)
                .expectRow(5, "Currency", 450.99)
                .expectRow(6, "Percent", 0.88)
                // TODO: can we tell a Date from Datetime? Parse String manually?
                .expectRow(7, "Date", LocalDateTime.of(2020, 1, 18, 0, 0, 0))
                .expectRow(8, "Datetime", LocalDateTime.of(2020, 1, 18, 15, 1, 55));

    }
}
