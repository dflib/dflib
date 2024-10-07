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

import static org.dflib.Exp.$col;
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

    @ParameterizedTest
    @ValueSource(strings = {"cardinality.xls", "cardinality.xlsx"})
    public void valueCardinality_Nulls(String source) throws IOException {
        try (InputStream in = getClass().getResourceAsStream(source)) {
            DataFrame df = new ExcelLoader().loadSheet(in, "Sheet1");

            new DataFrameAsserts(df, "A", "B")
                    .expectHeight(6)
                    .expectRow(0, 1.0, "ab")
                    .expectRow(1, 40000.0, "ab")
                    .expectRow(2, 40000.0, "bc")
                    .expectRow(3, 30000.0, "bc")
                    .expectRow(4, 30000.0, null)
                    .expectRow(5, null, "bc");

            DataFrame idCardinality = df.cols().select(
                    $col("A").mapVal(System::identityHashCode),
                    $col("B").mapVal(System::identityHashCode));

            // looks like POI interns deserialized Excel Strings (but not double or other types),
            // so String id cardinality is reduced by default
            assertEquals(6, idCardinality.getColumn(0).unique().size());
            assertEquals(3, idCardinality.getColumn(1).unique().size());
        }
    }

    @Test
    public void valueCardinality_compactCol_Name() throws IOException {

        try (InputStream in = getClass().getResourceAsStream("cardinality.xlsx")) {
            DataFrame df = new ExcelLoader()
                    .sheet("Sheet1", SheetLoader.of().compactCol("A").compactCol("B"))
                    .loadSheet(in, "Sheet1");

            new DataFrameAsserts(df, "A", "B")
                    .expectHeight(6)
                    .expectRow(0, 1.0, "ab")
                    .expectRow(1, 40000.0, "ab")
                    .expectRow(2, 40000.0, "bc")
                    .expectRow(3, 30000.0, "bc")
                    .expectRow(4, 30000.0, null)
                    .expectRow(5, null, "bc");

            DataFrame idCardinality = df.cols().select(
                    $col("A").mapVal(System::identityHashCode),
                    $col("B").mapVal(System::identityHashCode));

            assertEquals(4, idCardinality.getColumn(0).unique().size());
            assertEquals(3, idCardinality.getColumn(1).unique().size());
        }
    }

    @Test
    public void valueCardinality_compactCol_Name_MidRange() throws IOException {

        try (InputStream in = getClass().getResourceAsStream("cardinality-midrange.xlsx")) {
            DataFrame df = new ExcelLoader()
                    .sheet("Sheet1", SheetLoader.of()
                            // "A" is out of range and should be ignored
                            .compactCol("A")

                            // "B" is in range, but we leave it as sparse

                            // "C" and "D" are in range and should be compacted
                            .compactCol("C")
                            .compactCol("D"))
                    .loadSheet(in, "Sheet1");

            new DataFrameAsserts(df, "B", "C", "D")
                    .expectHeight(6)
                    .expectRow(0, 1.0, "ab", 1.0)
                    .expectRow(1, 40000.0, "ab", 40000.0)
                    .expectRow(2, 40000.0, "bc", 40000.0)
                    .expectRow(3, 30000.0, "bc", 30000.0)
                    .expectRow(4, 30000.0, null, 30000.0)
                    .expectRow(5, null, "bc", null);

            DataFrame idCardinality = df.cols().select(
                    $col("B").mapVal(System::identityHashCode),
                    $col("C").mapVal(System::identityHashCode),
                    $col("D").mapVal(System::identityHashCode));

            assertEquals(6, idCardinality.getColumn(0).unique().size());
            assertEquals(3, idCardinality.getColumn(1).unique().size());
            assertEquals(4, idCardinality.getColumn(2).unique().size());
        }
    }


    @Test
    public void valueCardinality_compactCol_Pos() throws IOException {

        try (InputStream in = getClass().getResourceAsStream("cardinality.xlsx")) {
            DataFrame df = new ExcelLoader()
                    .sheet("Sheet1", SheetLoader.of().compactCol(0).compactCol(1))
                    .loadSheet(in, "Sheet1");

            new DataFrameAsserts(df, "A", "B")
                    .expectHeight(6)
                    .expectRow(0, 1.0, "ab")
                    .expectRow(1, 40000.0, "ab")
                    .expectRow(2, 40000.0, "bc")
                    .expectRow(3, 30000.0, "bc")
                    .expectRow(4, 30000.0, null)
                    .expectRow(5, null, "bc");

            DataFrame idCardinality = df.cols().select(
                    $col("A").mapVal(System::identityHashCode),
                    $col("B").mapVal(System::identityHashCode));

            assertEquals(4, idCardinality.getColumn(0).unique().size());
            assertEquals(3, idCardinality.getColumn(1).unique().size());
        }
    }

    @Test
    public void valueCardinality_compactCol_Pos_MidRange() throws IOException {

        try (InputStream in = getClass().getResourceAsStream("cardinality-midrange.xlsx")) {
            DataFrame df = new ExcelLoader()
                    .sheet("Sheet1", SheetLoader.of()
                            // "0" is out of range and should be ignored
                            .compactCol(0)

                            // "1" is in range, but we leave it as sparse

                            // "2" and "3" are in range and should be compacted
                            .compactCol(2)
                            .compactCol(3))
                    .loadSheet(in, "Sheet1");

            new DataFrameAsserts(df, "B", "C", "D")
                    .expectHeight(6)
                    .expectRow(0, 1.0, "ab", 1.0)
                    .expectRow(1, 40000.0, "ab", 40000.0)
                    .expectRow(2, 40000.0, "bc", 40000.0)
                    .expectRow(3, 30000.0, "bc", 30000.0)
                    .expectRow(4, 30000.0, null, 30000.0)
                    .expectRow(5, null, "bc", null);

            DataFrame idCardinality = df.cols().select(
                    $col("B").mapVal(System::identityHashCode),
                    $col("C").mapVal(System::identityHashCode),
                    $col("D").mapVal(System::identityHashCode));

            assertEquals(6, idCardinality.getColumn(0).unique().size());
            assertEquals(3, idCardinality.getColumn(1).unique().size());
            assertEquals(4, idCardinality.getColumn(2).unique().size());
        }
    }

    @Test
    public void valueCardinality_compactCol_Name_MidRange_firstRowAsHeader() throws IOException {

        try (InputStream in = getClass().getResourceAsStream("cardinality-midrange-header.xlsx")) {
            DataFrame df = new ExcelLoader()
                    .sheet("Sheet1", SheetLoader.of()
                            .firstRowAsHeader()

                            // Out of range (and really undefined) and should be ignored
                            .compactCol("A")

                            // "X" is in range, but we leave it as sparse

                            // "Y" and "Z" are in range and should be compacted
                            .compactCol("Y")
                            .compactCol("Z"))
                    .loadSheet(in, "Sheet1");

            new DataFrameAsserts(df, "X", "Y", "Z")
                    .expectHeight(6)
                    .expectRow(0, 1.0, "ab", 1.0)
                    .expectRow(1, 40000.0, "ab", 40000.0)
                    .expectRow(2, 40000.0, "bc", 40000.0)
                    .expectRow(3, 30000.0, "bc", 30000.0)
                    .expectRow(4, 30000.0, null, 30000.0)
                    .expectRow(5, null, "bc", null);

            DataFrame idCardinality = df.cols().select(
                    $col("X").mapVal(System::identityHashCode),
                    $col("Y").mapVal(System::identityHashCode),
                    $col("Z").mapVal(System::identityHashCode));

            assertEquals(6, idCardinality.getColumn(0).unique().size());
            assertEquals(3, idCardinality.getColumn(1).unique().size());
            assertEquals(4, idCardinality.getColumn(2).unique().size());
        }
    }
}
