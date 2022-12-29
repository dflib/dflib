package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

public class DataFrame_FoldByColumnTest {

    @Test
    public void testArray() {
        DataFrame df = DataFrame.foldByColumn("a", "b").of("a", 1, "b", 2, "c", 3);

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 2)
                .expectRow(1, 1, "c")
                .expectRow(2, "b", 3);
    }

    @Test
    public void testArray_Partial1() {
        DataFrame df = DataFrame.foldByColumn("a", "b").of("a", 1, "b", 2, "c");

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 2)
                .expectRow(1, 1, "c")
                .expectRow(2, "b", null);
    }

    @Test
    public void testArray_Partial2() {
        DataFrame df = DataFrame.foldByColumn("a", "b", "c").of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j");

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectRow(0, "a", "e", "i")
                .expectRow(1, "b", "f", "j")
                .expectRow(2, "c", "g", null)
                .expectRow(3, "d", "h", null);
    }

    @Test
    public void testStream() {
        DataFrame df = DataFrame.foldByColumn("a", "b").ofStream(Stream.of("a", 1, "b", 2, "c", 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 2)
                .expectRow(1, 1, "c")
                .expectRow(2, "b", 3);
    }

    @Test
    public void testStream_Partial() {
        DataFrame df = DataFrame.foldByColumn("a", "b").ofStream(Stream.of("a", 1, "b", 2, "c"));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 2)
                .expectRow(1, 1, "c")
                .expectRow(2, "b", null);
    }

    @Test
    public void testIterable() {
        DataFrame df = DataFrame.foldByColumn("a", "b").ofIterable(asList("a", 1, "b", 2, "c", 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 2)
                .expectRow(1, 1, "c")
                .expectRow(2, "b", 3);
    }

    @Test
    public void testIterable_Partial() {
        DataFrame df = DataFrame.foldByColumn("a", "b").ofIterable(asList("a", 1, "b", 2, "c"));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 2)
                .expectRow(1, 1, "c")
                .expectRow(2, "b", null);
    }

    @Test
    public void testIntArray() {
        DataFrame df = DataFrame.foldByColumn("a", "b").ofInts(-9999, 0, 1, 2, 3, 4, 5);

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectIntColumns(0, 1)
                .expectRow(0, 0, 3)
                .expectRow(1, 1, 4)
                .expectRow(2, 2, 5);
    }

    @Test
    public void testIntArray_Partial() {
        DataFrame df = DataFrame.foldByColumn("a", "b").ofInts(-9999, 0, 1, 2, 3, 4);

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectIntColumns(0, 1)
                .expectRow(0, 0, 3)
                .expectRow(1, 1, 4)
                .expectRow(2, 2, -9999);
    }

    @Test
    public void testIntStream() {
        DataFrame df = DataFrame.foldByColumn("a", "b").ofStream(-9999, IntStream.of(-1, 1, 0, 2, 5, 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectIntColumns(0, 1)
                .expectRow(0, -1, 2)
                .expectRow(1, 1, 5)
                .expectRow(2, 0, 3);
    }

    @Test
    public void testIntStream_Partial() {
        DataFrame df = DataFrame.foldByColumn("a", "b").ofStream(-9999, IntStream.of(-1, 1, 0, 2, 5));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectIntColumns(0, 1)
                .expectRow(0, -1, 2)
                .expectRow(1, 1, 5)
                .expectRow(2, 0, -9999);
    }

    @Test
    public void testIntStream_Partial2() {
        DataFrame df = DataFrame.foldByColumn("a", "b", "c").ofStream(IntStream.range(0, 10));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectIntColumns(0, 1, 2)
                .expectRow(0, 0, 4, 8)
                .expectRow(1, 1, 5, 9)
                .expectRow(2, 2, 6, 0)
                .expectRow(3, 3, 7, 0);
    }

    @Test
    public void testLongArray() {
        DataFrame df = DataFrame.foldByColumn("a", "b").ofLongs(-9999, 0, 1, 2, 3, 4, 5);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, 0L, 3L)
                .expectRow(1, 1L, 4L)
                .expectRow(2, 2L, 5L);
    }

    @Test
    public void testLongArray_Partial() {
        DataFrame df = DataFrame.foldByColumn("a", "b").ofLongs(-9999, 0, 1, 2, 3, 4);

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, 0L, 3L)
                .expectRow(1, 1L, 4L)
                .expectRow(2, 2L, -9999L);
    }

    @Test
    public void testLongStream() {
        DataFrame df = DataFrame.foldByColumn("a", "b").ofStream(-9999, LongStream.of(-1, 1, 0, 2, 5, 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, -1L, 2L)
                .expectRow(1, 1L, 5L)
                .expectRow(2, 0L, 3L);
    }

    @Test
    public void testLongStream_Partial1() {
        DataFrame df = DataFrame.foldByColumn("a", "b").ofStream(-9999, LongStream.of(-1, 1, 0, 2, 5));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, -1L, 2L)
                .expectRow(1, 1L, 5L)
                .expectRow(2, 0L, -9999L);
    }

    @Test
    public void testLongStream_Partial2() {
        DataFrame df = DataFrame.foldByColumn("a", "b", "c").ofStream(LongStream.range(0, 10));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectLongColumns(0, 1, 2)
                .expectRow(0, 0L, 4L, 8L)
                .expectRow(1, 1L, 5L, 9L)
                .expectRow(2, 2L, 6L, 0L)
                .expectRow(3, 3L, 7L, 0L);
    }

    @Test
    public void testDoubleArray() {
        DataFrame df = DataFrame.foldByColumn("a", "b").ofDoubles(-9999.9, 0, 1.1, 2, 3, 4, 5);

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, 0., 3.)
                .expectRow(1, 1.1, 4.)
                .expectRow(2, 2., 5.);
    }

    @Test
    public void testDoubleArray_Partial() {
        DataFrame df = DataFrame.foldByColumn("a", "b").ofDoubles(-9999.9, 0, 1.1, 2, 3, 4);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, 0., 3.)
                .expectRow(1, 1.1, 4.)
                .expectRow(2, 2., -9999.9);
    }

    @Test
    public void testDoubleStream() {
        DataFrame df = DataFrame.foldByColumn("a", "b").ofStream(-9999.9, DoubleStream.of(-1, 1.1, 0, 2, 5, 3));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1., 2.)
                .expectRow(1, 1.1, 5.)
                .expectRow(2, 0., 3.);
    }

    @Test
    public void testDoubleStream_Partial1() {
        DataFrame df = DataFrame.foldByColumn("a", "b").ofStream(-9999.9, DoubleStream.of(-1, 1.1, 0, 2, 5));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1., 2.)
                .expectRow(1, 1.1, 5.)
                .expectRow(2, 0., -9999.9);
    }

    @Test
    public void testDoubleStream_Partial2() {
        DataFrame df = DataFrame.foldByColumn("a", "b", "c").ofStream(DoubleStream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectDoubleColumns(0, 1, 2)
                .expectRow(0, 0., 4., 8.)
                .expectRow(1, 1., 5., 9.)
                .expectRow(2, 2., 6., 0.)
                .expectRow(3, 3., 7., 0.);
    }
}
