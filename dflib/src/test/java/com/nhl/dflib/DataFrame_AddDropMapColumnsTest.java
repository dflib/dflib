package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

public class DataFrame_AddDropMapColumnsTest {

    @Test
    public void testMapColumn() {
        Series<Integer> mapped = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .mapColumn(r -> ((int) r.get(0)) * 10);

        new SeriesAsserts(mapped).expectData(10, 20);
    }

    @Test
    public void testAddColumn() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .addColumn("c", r -> ((int) r.get(0)) * 10);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x", 10)
                .expectRow(1, 2, "y", 20);
    }

    @Test
    public void testAddColumn_Sparse() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .selectColumns("a")
                .addColumn("c", r -> ((int) r.get(0)) * 10);

        new DataFrameAsserts(df, "a", "c")
                .expectHeight(2)
                .expectRow(0, 1, 10)
                .expectRow(1, 2, 20);
    }

    @Test
    public void testAddColumn_Series() {

        Series<String> column = Series.forData("m", "n");

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y").addColumn("c", column);


        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x", "m")
                .expectRow(1, 2, "y", "n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddColumn_Series_Shorter() {

        Series<String> column = Series.forData("m");
        DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y").addColumn("c", column);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddColumn_Series_Longer() {

        Series<String> column = Series.forData("m", "n", "o");

        DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y").addColumn("c", column);

    }

    @Test
    public void testAddColumns() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .addColumns(new String[]{"c", "d"},
                        r -> ((int) r.get(0)) * 10,
                        r -> ((int) r.get(0)) * 2);

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(2)
                .expectRow(0, 1, "x", 10, 2)
                .expectRow(1, 2, "y", 20, 4);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddColumns_SizeMismatch() {
        DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .addColumns(new String[]{"c", "d", "e"},
                        r -> ((int) r.get(0)) * 10,
                        r -> ((int) r.get(0)) * 2);
    }

    @Test
    public void testAddColumns_RowMapper() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                1,
                2)
                .addColumns(new String[]{"c", "d"},
                        (from, to) -> {
                            int i = (int) from.get(0);
                            to.set(0, i + 1);
                            to.set(1, i + 2);
                        });

        new DataFrameAsserts(df, "a", "c", "d")
                .expectHeight(2)
                .expectRow(0, 1, 2, 3)
                .expectRow(1, 2, 3, 4);
    }

    @Test
    public void testDropColumns1() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .dropColumns("a");

        new DataFrameAsserts(df, "b")
                .expectHeight(2)
                .expectRow(0, "x")
                .expectRow(1, "y");
    }

    @Test
    public void testDropColumns2() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .dropColumns("b");

        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 1)
                .expectRow(1, 2);
    }

    @Test
    public void testDropColumns3() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .dropColumns();

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void testDropColumns4() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .dropColumns("no_such_column");

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }

    @Test
    public void testDropColumns_Predicate() {
        DataFrame df = DataFrame.newFrame("a1", "b2", "c1")
                .foldByColumn(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .dropColumns(c -> c.endsWith("1"));

        new DataFrameAsserts(df, "b2")
                .expectHeight(3)
                .expectRow(0, 4)
                .expectRow(1, 5)
                .expectRow(2, 6);
    }

    @Test
    public void testAddRowNumberColumn() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .addRowNumberColumn("rn");

        new DataFrameAsserts(df, "a", "b", "rn")
                .expectHeight(2)
                .expectRow(0, 1, "x", 1)
                .expectRow(1, 2, "y", 2);
    }


    @Test
    @Deprecated
    public void testAddRowNumber() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .addRowNumber("rn");

        new DataFrameAsserts(df, "a", "b", "rn")
                .expectHeight(2)
                .expectRow(0, 1, "x", 0)
                .expectRow(1, 2, "y", 1);
    }

    @Test
    @Deprecated
    public void testAddRowNumber_StartValue() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .addRowNumber("rn", 2);

        new DataFrameAsserts(df, "a", "b", "rn")
                .expectHeight(2)
                .expectRow(0, 1, "x", 2)
                .expectRow(1, 2, "y", 3);
    }
}
