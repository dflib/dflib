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
    public void array() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(1, 2);
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1, 2);
    }

    @Test
    public void array_Partial() {

        DataFrame df = DataFrame.foldByRow("a", "b").of(1, 2, 3);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, 2)
                .expectRow(1, 3, null);
    }

    @Test
    public void stream() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .ofStream(Stream.of("a", 1, "b", 2, "c", 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }

    @Test
    public void stream_Partial() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .ofStream(Stream.of("a", 1, "b", 2, "c"));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", null);
    }

    @Test
    public void iterable() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .ofIterable(List.of("a", 1, "b", 2, "c", 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }

    @Test
    public void iterable_Partial() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .ofIterable(List.of("a", 1, "b", 2, "c"));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", null);
    }

    @Test
    public void intStream() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .ofStream(-9999, IntStream.of(-1, 1, 0, 2, 5, 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, -1, 1)
                .expectRow(1, 0, 2)
                .expectRow(2, 5, 3)
                .expectIntColumns(0, 1);
    }

    @Test
    public void intStream_Partial() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .ofStream(-9999, IntStream.of(-1, 1, 0, 2, 5));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, -1, 1)
                .expectRow(1, 0, 2)
                .expectRow(2, 5, -9999)
                .expectIntColumns(0, 1);

    }

    @Test
    public void longStream() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .ofStream(-9999L, LongStream.of(-1, 1, 0, 2, 5, 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, -1L, 1L)
                .expectRow(1, 0L, 2L)
                .expectRow(2, 5L, 3L);
    }

    @Test
    public void longStream_Partial() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .ofStream(-9999L, LongStream.of(-1, 1, 0, 2, 5));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, -1L, 1L)
                .expectRow(1, 0L, 2L)
                .expectRow(2, 5L, -9999L);

    }

    @Test
    public void doubleStream() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .ofStream(-9999.9, DoubleStream.of(-1, 1.1, 0, 2, 5, 3));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1., 1.1)
                .expectRow(1, 0., 2.)
                .expectRow(2, 5., 3.);
    }

    @Test
    public void doubleStream_Partial() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .ofStream(-9999.9, DoubleStream.of(-1, 1.1, 0, 2, 5));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1., 1.1)
                .expectRow(1, 0., 2.)
                .expectRow(2, 5., -9999.9);
    }

    @Test
    public void doubleStream_Partial_DefaultPadding() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .ofStream(DoubleStream.of(-1, 1.1, 0, 2, 5));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1., 1.1)
                .expectRow(1, 0., 2.)
                .expectRow(2, 5., 0.);
    }


}
