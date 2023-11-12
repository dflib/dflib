package com.nhl.dflib;

import com.nhl.dflib.builder.IntAccum;
import com.nhl.dflib.builder.ObjectAccum;
import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

@Deprecated(since = "0.16", forRemoval = true)
public class DataFrameBuilderTest {

    @Test
    public void empty() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b")).empty();
        new DataFrameAsserts(df, "a", "b").expectHeight(0);
    }

    @Test
    public void seriesColumns() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .columns(
                        Series.of("a", "b", "c"),
                        Series.ofInt(1, 2, 3)
                );

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }

    @Test
    public void byRow() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .byRow()
                .addRow("a", 1)
                .addRow("b", 2)
                .addRow("c", 3)
                .create();

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }

    @Test
    public void byRow_CustomAccums() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .byRow(new ObjectAccum<>(3), new IntAccum(3))
                .addRow("a", 1)
                .addRow("b", 2)
                .addRow("c", 3)
                .create();

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectIntColumns(1)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }

    @Test
    public void addRow() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .addRow("a", 1)
                .addRow("b", 2)
                .addRow("c", 3)
                .create();

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }

    @Test
    public void rows() {

        Object[][] rows = new Object[][]{
                {"a", 1},
                {"b", 2},
                {"c", 3}
        };

        DataFrame df = new DataFrameBuilder(Index.of("a", "b")).rows(rows);

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }

    @Test
    public void foldByRow() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldByRow("a", 1, "b", 2, "c", 3);

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }

    @Test
    public void foldByRow_Partial() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldByRow("a", 1, "b", 2, "c");

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", null);
    }

    @Test
    public void foldByColumn() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldByColumn("a", 1, "b", 2, "c", 3);

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 2)
                .expectRow(1, 1, "c")
                .expectRow(2, "b", 3);
    }

    @Test
    public void foldByColumn_Partial1() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldByColumn("a", 1, "b", 2, "c");

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 2)
                .expectRow(1, 1, "c")
                .expectRow(2, "b", null);
    }

    @Test
    public void foldByColumn_Partial2() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b", "c"))
                .foldByColumn("a", "b", "c", "d", "e", "f", "g", "h", "i", "j");

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectRow(0, "a", "e", "i")
                .expectRow(1, "b", "f", "j")
                .expectRow(2, "c", "g", null)
                .expectRow(3, "d", "h", null);
    }


    @Test
    public void foldStreamByRow() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldStreamByRow(Stream.of("a", 1, "b", 2, "c", 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }

    @Test
    public void foldStreamByRow_Partial() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldStreamByRow(Stream.of("a", 1, "b", 2, "c"));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", null);
    }

    @Test
    public void foldStreamByColumn() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldStreamByColumn(Stream.of("a", 1, "b", 2, "c", 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 2)
                .expectRow(1, 1, "c")
                .expectRow(2, "b", 3);
    }

    @Test
    public void foldStreamByColumn_Partial() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldStreamByColumn(Stream.of("a", 1, "b", 2, "c"));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 2)
                .expectRow(1, 1, "c")
                .expectRow(2, "b", null);
    }

    @Test
    public void foldIterableByRow() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldIterableByRow(asList("a", 1, "b", 2, "c", 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }

    @Test
    public void foldIterableByRow_Partial() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldIterableByRow(asList("a", 1, "b", 2, "c"));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", null);
    }

    @Test
    public void foldIterableByColumn() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldIterableByColumn(asList("a", 1, "b", 2, "c", 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 2)
                .expectRow(1, 1, "c")
                .expectRow(2, "b", 3);
    }

    @Test
    public void foldIterableByColumn_Partial() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldIterableByColumn(asList("a", 1, "b", 2, "c"));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 2)
                .expectRow(1, 1, "c")
                .expectRow(2, "b", null);
    }

    @Test
    public void objectsToRows() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .objectsToRows(asList("a", "bc", "def"), s -> new Object[]{s, s.length()});

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, "a", 1)
                .expectRow(1, "bc", 2)
                .expectRow(2, "def", 3);
    }

    @Test
    public void foldIntByColumn() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldIntByColumn(-9999, 0, 1, 2, 3, 4, 5);

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectIntColumns(0, 1)
                .expectRow(0, 0, 3)
                .expectRow(1, 1, 4)
                .expectRow(2, 2, 5);
    }

    @Test
    public void foldIntByColumn_Partial() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldIntByColumn(-9999, 0, 1, 2, 3, 4);

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectIntColumns(0, 1)
                .expectRow(0, 0, 3)
                .expectRow(1, 1, 4)
                .expectRow(2, 2, -9999);
    }


    @Test
    public void foldIntStreamByRow() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldIntStreamByRow(-9999, IntStream.of(-1, 1, 0, 2, 5, 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, -1, 1)
                .expectRow(1, 0, 2)
                .expectRow(2, 5, 3)
                .expectIntColumns(0, 1);
    }

    @Test
    public void foldIntStreamByRow_Partial() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldIntStreamByRow(-9999, IntStream.of(-1, 1, 0, 2, 5));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectRow(0, -1, 1)
                .expectRow(1, 0, 2)
                .expectRow(2, 5, -9999)
                .expectIntColumns(0, 1);

    }

    @Test
    public void foldIntStreamByColumn() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldIntStreamByColumn(-9999, IntStream.of(-1, 1, 0, 2, 5, 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectIntColumns(0, 1)
                .expectRow(0, -1, 2)
                .expectRow(1, 1, 5)
                .expectRow(2, 0, 3);
    }

    @Test
    public void foldIntStreamByColumn_Partial1() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldIntStreamByColumn(-9999, IntStream.of(-1, 1, 0, 2, 5));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectIntColumns(0, 1)
                .expectRow(0, -1, 2)
                .expectRow(1, 1, 5)
                .expectRow(2, 0, -9999);
    }

    @Test
    public void foldIntStreamByColumn_Partial2() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b", "c"))
                .foldIntStreamByColumn(IntStream.range(0, 10));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectIntColumns(0, 1, 2)
                .expectRow(0, 0, 4, 8)
                .expectRow(1, 1, 5, 9)
                .expectRow(2, 2, 6, 0)
                .expectRow(3, 3, 7, 0);
    }

    @Test
    public void foldLongByColumn() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldLongByColumn(-9999, 0, 1, 2, 3, 4, 5);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, 0L, 3L)
                .expectRow(1, 1L, 4L)
                .expectRow(2, 2L, 5L);
    }

    @Test
    public void foldLongByColumn_Partial() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldLongByColumn(-9999, 0, 1, 2, 3, 4);

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, 0L, 3L)
                .expectRow(1, 1L, 4L)
                .expectRow(2, 2L, -9999L);
    }

    @Test
    public void foldLongStreamByRow() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldLongStreamByRow(-9999L, LongStream.of(-1, 1, 0, 2, 5, 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, -1L, 1L)
                .expectRow(1, 0L, 2L)
                .expectRow(2, 5L, 3L);
    }

    @Test
    public void foldLongStreamByRow_Partial() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldLongStreamByRow(-9999L, LongStream.of(-1, 1, 0, 2, 5));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, -1L, 1L)
                .expectRow(1, 0L, 2L)
                .expectRow(2, 5L, -9999L);

    }

    @Test
    public void foldLongStreamByColumn() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldLongStreamByColumn(-9999, LongStream.of(-1, 1, 0, 2, 5, 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, -1L, 2L)
                .expectRow(1, 1L, 5L)
                .expectRow(2, 0L, 3L);
    }

    @Test
    public void foldLongStreamByColumn_Partial1() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldLongStreamByColumn(-9999, LongStream.of(-1, 1, 0, 2, 5));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectLongColumns(0, 1)
                .expectRow(0, -1L, 2L)
                .expectRow(1, 1L, 5L)
                .expectRow(2, 0L, -9999L);
    }

    @Test
    public void foldLongStreamByColumn_Partial2() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b", "c"))
                .foldLongStreamByColumn(LongStream.range(0, 10));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectLongColumns(0, 1, 2)
                .expectRow(0, 0L, 4L, 8L)
                .expectRow(1, 1L, 5L, 9L)
                .expectRow(2, 2L, 6L, 0L)
                .expectRow(3, 3L, 7L, 0L);
    }

    @Test
    public void foldDoubleByColumn() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldDoubleByColumn(-9999.9, 0, 1.1, 2, 3, 4, 5);

        new DataFrameAsserts(df, "a", "b").expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, 0., 3.)
                .expectRow(1, 1.1, 4.)
                .expectRow(2, 2., 5.);
    }

    @Test
    public void foldDoubleByColumn_Partial() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldDoubleByColumn(-9999.9, 0, 1.1, 2, 3, 4);

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, 0., 3.)
                .expectRow(1, 1.1, 4.)
                .expectRow(2, 2., -9999.9);
    }

    @Test
    public void foldDoubleStreamByRow() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldDoubleStreamByRow(-9999.9, DoubleStream.of(-1, 1.1, 0, 2, 5, 3));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1., 1.1)
                .expectRow(1, 0., 2.)
                .expectRow(2, 5., 3.);
    }

    @Test
    public void foldDoubleStreamByRow_Partial() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldDoubleStreamByRow(-9999.9, DoubleStream.of(-1, 1.1, 0, 2, 5));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1., 1.1)
                .expectRow(1, 0., 2.)
                .expectRow(2, 5., -9999.9);
    }

    @Test
    public void foldDoubleStreamByRow_Partial_DefaultPadding() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldDoubleStreamByRow(DoubleStream.of(-1, 1.1, 0, 2, 5));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1., 1.1)
                .expectRow(1, 0., 2.)
                .expectRow(2, 5., 0.);
    }

    @Test
    public void foldDoubleStreamByColumn() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldDoubleStreamByColumn(-9999.9, DoubleStream.of(-1, 1.1, 0, 2, 5, 3));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1., 2.)
                .expectRow(1, 1.1, 5.)
                .expectRow(2, 0., 3.);
    }

    @Test
    public void foldDoubleStreamByColumn_Partial1() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b"))
                .foldDoubleStreamByColumn(-9999.9, DoubleStream.of(-1, 1.1, 0, 2, 5));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectDoubleColumns(0, 1)
                .expectRow(0, -1., 2.)
                .expectRow(1, 1.1, 5.)
                .expectRow(2, 0., -9999.9);
    }

    @Test
    public void foldDoubleStreamByColumn_Partial2() {
        DataFrame df = new DataFrameBuilder(Index.of("a", "b", "c"))
                .foldDoubleStreamByColumn(DoubleStream.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(4)
                .expectDoubleColumns(0, 1, 2)
                .expectRow(0, 0., 4., 8.)
                .expectRow(1, 1., 5., 9.)
                .expectRow(2, 2., 6., 0.)
                .expectRow(3, 3., 7., 0.);
    }

}
