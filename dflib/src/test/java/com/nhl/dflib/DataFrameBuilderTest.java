package com.nhl.dflib;

import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;

import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class DataFrameBuilderTest {

    @Test
    public void testEmpty() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .empty();

        new DFAsserts(df, "a", "b").expectHeight(0);
    }

    @Test
    public void testSeriesColumns() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .columns(
                        Series.forData("a", "b", "c"),
                        IntSeries.forInts(1, 2, 3)
                );

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }

    @Test
    public void testAddRow() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .addRow("a", 1)
                .addRow("b", 2)
                .addRow("c", 3)
                .create();

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }

    @Test
    public void testRows() {

        Object[][] rows = new Object[][]{
                {"a", 1},
                {"b", 2},
                {"c", 3}
        };

        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .rows(rows);

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }

    @Test
    public void testFoldByRow() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldByRow("a", 1, "b", 2, "c", 3);

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }

    @Test
    public void testFoldByRow_Partial() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldByRow("a", 1, "b", 2, "c");

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", null);
    }

    @Test
    public void testFoldByColumn() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldByColumn("a", 1, "b", 2, "c", 3);

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 2)
                .expectRow(1, 1, "c")
                .expectRow(2, "b", 3);
    }

    @Test
    public void testFoldByColumn_Partial() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldByColumn("a", 1, "b", 2, "c");

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 2)
                .expectRow(1, 1, "c")
                .expectRow(2, "b", null);
    }

    @Test
    public void testFoldStreamByRow() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldStreamByRow(Stream.of("a", 1, "b", 2, "c", 3));

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }

    @Test
    public void testFoldStreamByRow_Partial() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldStreamByRow(Stream.of("a", 1, "b", 2, "c"));

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", null);
    }

    @Test
    public void testFoldStreamByColumn() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldStreamByColumn(Stream.of("a", 1, "b", 2, "c", 3));

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 2)
                .expectRow(1, 1, "c")
                .expectRow(2, "b", 3);
    }

    @Test
    public void testFoldStreamByColumn_Partial() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldStreamByColumn(Stream.of("a", 1, "b", 2, "c"));

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 2)
                .expectRow(1, 1, "c")
                .expectRow(2, "b", null);
    }

    @Test
    public void testFoldIterableByRow() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldIterableByRow(asList("a", 1, "b", 2, "c", 3));

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }

    @Test
    public void testFoldIterableByRow_Partial() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldIterableByRow(asList("a", 1, "b", 2, "c"));

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", null);
    }

    @Test
    public void testFoldIterableByColumn() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldIterableByColumn(asList("a", 1, "b", 2, "c", 3));

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 2)
                .expectRow(1, 1, "c")
                .expectRow(2, "b", 3);
    }

    @Test
    public void testFoldIterableByColumn_Partial() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldIterableByColumn(asList("a", 1, "b", 2, "c"));

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 2)
                .expectRow(1, 1, "c")
                .expectRow(2, "b", null);
    }

    @Test
    public void testObjectsToRows() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .objectsToRows(asList("a", "bc", "def"), s -> new Object[]{s, s.length()});

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "bc", 2)
                .expectRow(2, "def", 3);
    }
}
