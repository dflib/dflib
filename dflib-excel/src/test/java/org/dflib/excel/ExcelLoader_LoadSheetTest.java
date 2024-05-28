package org.dflib.excel;

import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ExcelLoader_LoadSheetTest {

    @Test
    public void notFound() {
        File file = new File("no-such-file.xlsx");

        try {
            new ExcelLoader().loadSheet(file, 3);
            fail("Expected exception was not thrown");
        } catch (RuntimeException e) {
            assertEquals("Error reading file: no-such-file.xlsx", e.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"multi-sheet.xls", "multi-sheet.xlsx"})
    public void fromStream_MultiSheet(String source) throws IOException {

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
    public void fromFile_MultiSheet(String source) throws URISyntaxException {

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
    public void fromFile_MultiSheet_ByPosition(String source) throws URISyntaxException {

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
    public void invalidSheetName() throws URISyntaxException {
        File file = new File(getClass().getResource("multi-sheet.xlsx").toURI());
        assertThrows(RuntimeException.class, () -> new ExcelLoader().loadSheet(file, "No_such_Sheet"));
    }

    @Test
    public void dataTypes() throws URISyntaxException {

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

    @Test
    public void sparse() throws IOException {

        try (InputStream in = getClass().getResourceAsStream("sparse.xlsx")) {
            DataFrame df = new ExcelLoader().loadSheet(in, "Sheet1");

            new DataFrameAsserts(df, "B", "C", "D", "E", "F", "G")
                    .expectHeight(4)
                    .expectRow(0, 1d, 2d, null, null, 3d, null)
                    .expectRow(1, null, 4d, 5d, null, null, 6d)
                    .expectRow(2, null, null, null, null, null, null)
                    .expectRow(3, null, 3d, 8d, null, 7d, null);
        }
    }

    @Test
    public void phantomRowsOnly() throws URISyntaxException {
        // "phantom-trailing-rows.xlsx" contains only phantom rows that are included by POI in the
        // rows iterator, but need to be skipped by DFLib
        File file = new File(getClass().getResource("phantom-rows-only.xlsx").toURI());

        DataFrame df = new ExcelLoader().loadSheet(file, 0);

        new DataFrameAsserts(df).expectHeight(0);
    }

    @Test
    public void phantomLeadingRows() throws URISyntaxException {
        // "phantom-leading-rows.xlsx" contains leading phantom rows that are included by POI in the
        // rows iterator, but need to be skipped by DFLib
        File file = new File(getClass().getResource("phantom-leading-rows.xlsx").toURI());

        DataFrame df = new ExcelLoader().loadSheet(file, 0);

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(4)
                .expectRow(0, "a", null)
                .expectRow(1, "b", "c")
                .expectRow(2, null, null)
                .expectRow(3, "c", "d");
    }

    @Test
    public void phantomTrailingRows() throws URISyntaxException {
        // "phantom-trailing-rows.xlsx" contains trailing phantom rows that are included by POI in the
        // rows iterator, but need to be skipped by DFLib
        File file = new File(getClass().getResource("phantom-trailing-rows.xlsx").toURI());

        DataFrame df = new ExcelLoader().loadSheet(file, 0);

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(4)
                .expectRow(0, "a", null)
                .expectRow(1, "b", "c")
                .expectRow(2, null, null)
                .expectRow(3, "c", "d");
    }

    @Test
    public void fromStream_FirstRowAsHeader() throws IOException {

        try (InputStream in = getClass().getResourceAsStream("one-sheet.xlsx")) {

            DataFrame s1 = new ExcelLoader().firstRowAsHeader().loadSheet(in, "Sheet1");
            new DataFrameAsserts(s1, "One", "Two")
                    .expectHeight(1)
                    .expectRow(0, "Three", "Four");
        }
    }

    @Test
    public void fromStream_FirstRowAsHeader_Offset() throws IOException {

        try (InputStream in = getClass().getResourceAsStream("one-sheet-offset.xlsx")) {

            DataFrame s1 = new ExcelLoader().offset(2).firstRowAsHeader().loadSheet(in, "Sheet1");
            new DataFrameAsserts(s1, "t1", "t2")
                    .expectHeight(2)
                    .expectRow(0, "One", "Two")
                    .expectRow(1, "Three", "Four");
        }
    }

    @Test
    public void fromStream_FirstRowAsHeader_Offset_Limit() throws IOException {

        try (InputStream in = getClass().getResourceAsStream("one-sheet-offset.xlsx")) {

            DataFrame s1 = new ExcelLoader().offset(2).limit(1).firstRowAsHeader().loadSheet(in, "Sheet1");
            new DataFrameAsserts(s1, "t1", "t2")
                    .expectHeight(1)
                    .expectRow(0, "One", "Two");
        }
    }

    @Test
    public void firstRowAsHeader_EmptyColumnNames() throws IOException {

        try (InputStream in = getClass().getResourceAsStream("empty-column-names.xlsx")) {

            DataFrame s1 = new ExcelLoader().firstRowAsHeader().loadSheet(in, "Sheet1");
            new DataFrameAsserts(s1, "c1", "c2", "C", "D")
                    .expectHeight(1)
                    .expectRow(0, "v1", "v2", "v3", "v4");
        }
    }

    @Test
    public void fromStream_Offset_Empty() throws IOException {

        try (InputStream in = getClass().getResourceAsStream("one-sheet.xlsx")) {
            DataFrame s1 = new ExcelLoader().offset(2).loadSheet(in, "Sheet1");
            new DataFrameAsserts(s1, "A", "B").expectHeight(0);
        }
    }

    @Test
    public void fromStream_Limit_Empty() throws IOException {

        try (InputStream in = getClass().getResourceAsStream("one-sheet.xlsx")) {
            DataFrame s1 = new ExcelLoader().limit(0).loadSheet(in, "Sheet1");
            new DataFrameAsserts(s1, "A", "B").expectHeight(0);
        }
    }
}
