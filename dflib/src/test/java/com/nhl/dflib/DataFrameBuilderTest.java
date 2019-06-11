package com.nhl.dflib;

import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
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
    public void testFoldByColumn_Partial1() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldByColumn("a", 1, "b", 2, "c");

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 2)
                .expectRow(1, 1, "c")
                .expectRow(2, "b", null);
    }

    @Test
    public void testFoldByColumn_Partial2() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b", "c")
                .foldByColumn("a", "b", "c", "d", "e", "f", "g", "h", "i", "j");

        new DFAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectRow(0, "a", "e", "i")
                .expectRow(1, "b", "f", "j")
                .expectRow(2, "c", "g", null)
                .expectRow(3, "d", "h", null);
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

    @Test
    public void testFoldIntByColumn() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldIntByColumn(-9999, 0, 1, 2, 3, 4, 5);

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectIntColumns(0, 1)
                .expectRow(0, 0, 3)
                .expectRow(1, 1, 4)
                .expectRow(2, 2, 5);
    }

    @Test
    public void testFoldIntByColumn_Partial() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldIntByColumn(-9999, 0, 1, 2, 3, 4);

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectIntColumns(0, 1)
                .expectRow(0, 0, 3)
                .expectRow(1, 1, 4)
                .expectRow(2, 2, -9999);
    }


    @Test
    public void testFoldIntStreamByRow() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldIntStreamByRow(-9999, IntStream.of(-1, 1, 0, 2, 5, 3));

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, -1, 1)
                .expectRow(1, 0, 2)
                .expectRow(2, 5, 3)
                .expectIntColumns(0, 1);
    }

    @Test
    public void testFoldIntStreamByRow_Partial() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldIntStreamByRow(-9999, IntStream.of(-1, 1, 0, 2, 5));

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, -1, 1)
                .expectRow(1, 0, 2)
                .expectRow(2, 5, -9999)
                .expectIntColumns(0, 1);

    }

    @Test
    public void testFoldIntStreamByColumn() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldIntStreamByColumn(-9999, IntStream.of(-1, 1, 0, 2, 5, 3));

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectIntColumns(0, 1)
                .expectRow(0, -1, 2)
                .expectRow(1, 1, 5)
                .expectRow(2, 0, 3);
    }

    @Test
    public void testFoldIntStreamByColumn_Partial1() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldIntStreamByColumn(-9999, IntStream.of(-1, 1, 0, 2, 5));

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectIntColumns(0, 1)
                .expectRow(0, -1, 2)
                .expectRow(1, 1, 5)
                .expectRow(2, 0, -9999);
    }

    @Test
    public void testFoldIntStreamByColumn_Partial2() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b", "c")
                .foldIntStreamByColumn(IntStream.range(0, 10));

        new DFAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectIntColumns(0, 1, 2)
                .expectRow(0, 0, 4, 8)
                .expectRow(1, 1, 5, 9)
                .expectRow(2, 2, 6, 0)
                .expectRow(3, 3, 7, 0);
    }

    @Test
    public void testFoldLongByColumn() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldLongByColumn(-9999, 0, 1, 2, 3, 4, 5);

        new DFAsserts(df, "a", "b")
                .expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, 0L, 3L)
                .expectRow(1, 1L, 4L)
                .expectRow(2, 2L, 5L);
    }

    @Test
    public void testFoldLongByColumn_Partial() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldLongByColumn(-9999, 0, 1, 2, 3, 4);

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, 0L, 3L)
                .expectRow(1, 1L, 4L)
                .expectRow(2, 2L, -9999L);
    }

    @Test
    public void testFoldLongStreamByRow() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldLongStreamByRow(-9999L, LongStream.of(-1, 1, 0, 2, 5, 3));

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, -1L, 1L)
                .expectRow(1, 0L, 2L)
                .expectRow(2, 5L, 3L);
    }

    @Test
    public void testFoldLongStreamByRow_Partial() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldLongStreamByRow(-9999L, LongStream.of(-1, 1, 0, 2, 5));

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, -1L, 1L)
                .expectRow(1, 0L, 2L)
                .expectRow(2, 5L, -9999L);

    }

    @Test
    public void testFoldLongStreamByColumn() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldLongStreamByColumn(-9999, LongStream.of(-1, 1, 0, 2, 5, 3));

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, -1L, 2L)
                .expectRow(1, 1L, 5L)
                .expectRow(2, 0L, 3L);
    }

    @Test
    public void testFoldLongStreamByColumn_Partial1() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldLongStreamByColumn(-9999, LongStream.of(-1, 1, 0, 2, 5));

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, -1L, 2L)
                .expectRow(1, 1L, 5L)
                .expectRow(2, 0L, -9999L);
    }

    @Test
    public void testFoldLongStreamByColumn_Partial2() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b", "c")
                .foldLongStreamByColumn(LongStream.range(0, 10));

        new DFAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectLongColumns(0, 1, 2)
                .expectRow(0, 0L, 4L, 8L)
                .expectRow(1, 1L, 5L, 9L)
                .expectRow(2, 2L, 6L, 0L)
                .expectRow(3, 3L, 7L, 0L);
    }

    @Test
    public void testFoldDoubleByColumn() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldDoubleByColumn(-9999.9, 0, 1.1, 2, 3, 4, 5);

        new DFAsserts(df, "a", "b").expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, 0., 3.)
                .expectRow(1, 1.1, 4.)
                .expectRow(2, 2., 5.);
    }

    @Test
    public void testFoldDoubleByColumn_Partial() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldDoubleByColumn(-9999.9, 0, 1.1, 2, 3, 4);

        new DFAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, 0., 3.)
                .expectRow(1, 1.1, 4.)
                .expectRow(2, 2., -9999.9);
    }

    @Test
    public void testFoldDoubleStreamByRow() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldDoubleStreamByRow(-9999.9, DoubleStream.of(-1, 1.1, 0, 2, 5, 3));

        new DFAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1., 1.1)
                .expectRow(1, 0., 2.)
                .expectRow(2, 5., 3.);
    }

    @Test
    public void testFoldDoubleStreamByRow_Partial() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldDoubleStreamByRow(-9999.9, DoubleStream.of(-1, 1.1, 0, 2, 5));

        new DFAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1., 1.1)
                .expectRow(1, 0., 2.)
                .expectRow(2, 5., -9999.9);
    }

    @Test
    public void testFoldDoubleStreamByRow_Partial_DefaultPadding() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldDoubleStreamByRow(DoubleStream.of(-1, 1.1, 0, 2, 5));

        new DFAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1., 1.1)
                .expectRow(1, 0., 2.)
                .expectRow(2, 5., 0.);
    }

    @Test
    public void testFoldDoubleStreamByColumn() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldDoubleStreamByColumn(-9999.9, DoubleStream.of(-1, 1.1, 0, 2, 5, 3));

        new DFAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1., 2.)
                .expectRow(1, 1.1, 5.)
                .expectRow(2, 0., 3.);
    }

    @Test
    public void testFoldDoubleStreamByColumn_Partial1() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b")
                .foldDoubleStreamByColumn(-9999.9, DoubleStream.of(-1, 1.1, 0, 2, 5));

        new DFAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1., 2.)
                .expectRow(1, 1.1, 5.)
                .expectRow(2, 0., -9999.9);
    }

    @Test
    public void testFoldDoubleStreamByColumn_Partial2() {
        DataFrame df = DataFrameBuilder
                .builder("a", "b", "c")
                .foldDoubleStreamByColumn(DoubleStream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));

        new DFAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectDoubleColumns(0, 1, 2)
                .expectRow(0, 0., 4., 8.)
                .expectRow(1, 1., 5., 9.)
                .expectRow(2, 2., 6., 0.)
                .expectRow(3, 3., 7., 0.);
    }

}
