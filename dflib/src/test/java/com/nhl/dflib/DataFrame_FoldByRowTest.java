package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class DataFrame_FoldByRowTest {

    @Test
    public void testArray() {
        DataFrame df = DataFrame.foldByRow("a", "b").array(1, 2);
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1, 2);
    }

    @Test
    public void testArray_Partial() {

        DataFrame df = DataFrame.foldByRow("a", "b").array(1, 2, 3);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, null);
    }

    @Test
    public void testStream() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .stream(Stream.of("a", 1, "b", 2, "c", 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }

    @Test
    public void testStream_Partial() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .stream(Stream.of("a", 1, "b", 2, "c"));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", null);
    }

    @Test
    public void testIterable() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .iterable(List.of("a", 1, "b", 2, "c", 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }

    @Test
    public void testIterable_Partial() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .iterable(List.of("a", 1, "b", 2, "c"));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", null);
    }

    @Test
    public void testIntStream() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .intStream(-9999, IntStream.of(-1, 1, 0, 2, 5, 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, -1, 1)
                .expectRow(1, 0, 2)
                .expectRow(2, 5, 3)
                .expectIntColumns(0, 1);
    }

    @Test
    public void testIntStream_Partial() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .intStream(-9999, IntStream.of(-1, 1, 0, 2, 5));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, -1, 1)
                .expectRow(1, 0, 2)
                .expectRow(2, 5, -9999)
                .expectIntColumns(0, 1);

    }

    @Test
    public void testLongStream() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .longStream(-9999L, LongStream.of(-1, 1, 0, 2, 5, 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, -1L, 1L)
                .expectRow(1, 0L, 2L)
                .expectRow(2, 5L, 3L);
    }

    @Test
    public void testLongStream_Partial() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .longStream(-9999L, LongStream.of(-1, 1, 0, 2, 5));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, -1L, 1L)
                .expectRow(1, 0L, 2L)
                .expectRow(2, 5L, -9999L);

    }

    @Test
    public void testDoubleStream() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .doubleStream(-9999.9, DoubleStream.of(-1, 1.1, 0, 2, 5, 3));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1., 1.1)
                .expectRow(1, 0., 2.)
                .expectRow(2, 5., 3.);
    }

    @Test
    public void testDoubleStream_Partial() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .doubleStream(-9999.9, DoubleStream.of(-1, 1.1, 0, 2, 5));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1., 1.1)
                .expectRow(1, 0., 2.)
                .expectRow(2, 5., -9999.9);
    }

    @Test
    public void testDoubleStream_Partial_DefaultPadding() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .doubleStream(DoubleStream.of(-1, 1.1, 0, 2, 5));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1., 1.1)
                .expectRow(1, 0., 2.)
                .expectRow(2, 5., 0.);
    }


}
