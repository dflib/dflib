package com.nhl.dflib;

import com.nhl.dflib.series.IntArraySeries;
import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;

import static java.util.Arrays.asList;

public class DataFrame_SelectTest {

    @Test
    public void testSelectColumns() {
        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                1, "x",
                2, "y")
                .selectColumns("b");

        new DFAsserts(df, "b")
                .expectHeight(2)
                .expectRow(0, "x")
                .expectRow(1, "y");
    }

    @Test
    public void testSelectColumns_DuplicateColumn() {
        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                1, "x",
                2, "y")
                .selectColumns("b", "b", "b");

        new DFAsserts(df, "b", "b_", "b__")
                .expectHeight(2)
                .expectRow(0, "x", "x", "x")
                .expectRow(1, "y", "y", "y");
    }

    @Test
    public void testSelect_ints() {
        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                5, "x",
                9, "y",
                1, "z")
                .selectRows(0, 2);

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 5, "x")
                .expectRow(1, 1, "z");
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSelect_ints_out_of_range() {
        DataFrame.builder("a", "b").foldByRow(
                5, "x",
                9, "y",
                1, "z")
                .selectRows(0, 3)
                .materialize();
    }

    @Deprecated
    @Test
    public void testSelect_List() {
        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                5, "x",
                9, "y",
                1, "z")
                .select(asList(0, 2));

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 5, "x")
                .expectRow(1, 1, "z");
    }

    @Deprecated
    @Test
    public void testSelect_Series() {
        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                5, "x",
                9, "y",
                1, "z")
                .select(Series.forData(0, 2));

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 5, "x")
                .expectRow(1, 1, "z");
    }

    @Test
    public void testSelect_IntSeries() {
        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                5, "x",
                9, "y",
                1, "z")
                .selectRows(new IntArraySeries(0, 2));

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 5, "x")
                .expectRow(1, 1, "z");
    }

    @Test
    public void testSelect_reorder() {
        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                5, "x",
                9, "y",
                1, "z")
                .selectRows(2, 1);

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "z")
                .expectRow(1, 9, "y");
    }

    @Test
    public void testSelect_duplicate() {
        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                5, "x",
                9, "y",
                1, "z")
                .selectRows(2, 1, 1, 2);

        new DFAsserts(df, "a", "b")
                .expectHeight(4)
                .expectRow(0, 1, "z")
                .expectRow(1, 9, "y")
                .expectRow(1, 9, "y")
                .expectRow(0, 1, "z");
    }

    @Test
    public void testSelectColumnsByIndex() {

        DataFrame df = DataFrame.builder("a", "b", "c").foldByRow(
                1, "x", "m",
                2, "y", "n")
                .selectColumns(Index.forLabels("b", "a"));

        new DFAsserts(df, "b", "a")
                .expectHeight(2)
                .expectRow(0, "x", 1)
                .expectRow(1, "y", 2);
    }
}
