package com.nhl.dflib;

import com.nhl.dflib.unit.DFAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

public class DataFrame_AddDropMapColumnsTest {

    @Test
    public void testMapColumn() {
        Series<Integer> mapped = DataFrame.builder("a", "b").foldByRow(
                1, "x",
                2, "y")
                .mapColumn(r -> ((int) r.get(0)) * 10);

        new SeriesAsserts(mapped).expectData(10, 20);
    }

    @Test
    public void testAddColumn() {
        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                1, "x",
                2, "y")
                .addColumn("c", r -> ((int) r.get(0)) * 10);

        new DFAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x", 10)
                .expectRow(1, 2, "y", 20);
    }

    @Test
    public void testAddColumn_Sparse() {
        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                1, "x",
                2, "y")
                .selectColumns("a")
                .addColumn("c", r -> ((int) r.get(0)) * 10);

        new DFAsserts(df, "a", "c")
                .expectHeight(2)
                .expectRow(0, 1, 10)
                .expectRow(1, 2, 20);
    }

    @Test
    public void testAddColumn_Series() {

        Series<String> column = Series.forData("m", "n");

        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                1, "x",
                2, "y").addColumn("c", column);


        new DFAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x", "m")
                .expectRow(1, 2, "y", "n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddColumn_Series_Shorter() {

        Series<String> column = Series.forData("m");
        DataFrame.builder("a", "b").foldByRow(
                1, "x",
                2, "y").addColumn("c", column);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddColumn_Series_Longer() {

        Series<String> column = Series.forData("m", "n", "o");

        DataFrame.builder("a", "b").foldByRow(
                1, "x",
                2, "y").addColumn("c", column);

    }

    @Test
    public void testDropColumns1() {
        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                1, "x",
                2, "y")
                .dropColumns("a");

        new DFAsserts(df, "b")
                .expectHeight(2)
                .expectRow(0, "x")
                .expectRow(1, "y");
    }

    @Test
    public void testDropColumns2() {
        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                1, "x",
                2, "y")
                .dropColumns("b");

        new DFAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 1)
                .expectRow(1, 2);
    }

    @Test
    public void testDropColumns3() {
        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                1, "x",
                2, "y")
                .dropColumns();

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void testDropColumns4() {
        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                1, "x",
                2, "y")
                .dropColumns("no_such_column");

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void testAddRowNumber() {
        DataFrame df = DataFrame.builder("a", "b").foldByRow(
                1, "x",
                2, "y")
                .addRowNumber("rn");

        new DFAsserts(df, "a", "b", "rn")
                .expectHeight(2)
                .expectRow(0, 1, "x", 0)
                .expectRow(1, 2, "y", 1);
    }
}
