package com.nhl.dflib;

import org.junit.Test;

public class DataFrame_AddDropSelectColumnsTest extends BaseDataFrameTest {

    @Test
    public void testAddColumn() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = createDf(i1,
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
        Index i1 = Index.withNames("a", "b");
        DataFrame df = createDf(i1,
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

        Series<String> column = Series.from("m", "n");

        Index i1 = Index.withNames("a", "b");
        DataFrame df = createDf(i1,
                1, "x",
                2, "y").addColumn("c", column);


        new DFAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, "x", "m")
                .expectRow(1, 2, "y", "n");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddColumn_Series_Shorter() {

        Series<String> column = Series.from("m");

        Index i1 = Index.withNames("a", "b");
        createDf(i1,
                1, "x",
                2, "y").addColumn("c", column);

    }
    @Test(expected = IllegalArgumentException.class)
    public void testAddColumn_Series_Longer() {

        Series<String> column = Series.from("m", "n", "o");

        Index i1 = Index.withNames("a", "b");
        createDf(i1,
                1, "x",
                2, "y").addColumn("c", column);

    }

    @Test
    public void testSelectColumns() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = createDf(i1,
                1, "x",
                2, "y")
                .selectColumns("b");

        IndexPosition expectedPos = new IndexPosition(0, 0, "b");

        new DFAsserts(df, expectedPos)
                .expectHeight(2)
                .expectRow(0, "x")
                .expectRow(1, "y");
    }

    @Test
    public void testSelectColumns_DuplicateColumn() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = createDf(i1,
                1, "x",
                2, "y")
                .selectColumns("b", "b", "b");

        IndexPosition pos0 = new IndexPosition(0, 0, "b");
        IndexPosition pos1 = new IndexPosition(1, 1, "b_");
        IndexPosition pos2 = new IndexPosition(2, 2, "b__");

        new DFAsserts(df, pos0, pos1, pos2)
                .expectHeight(2)
                .expectRow(0, "x", "x", "x")
                .expectRow(1, "y", "y", "y");
    }

    @Test
    public void testDropColumns1() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = createDf(i1,
                1, "x",
                2, "y")
                .dropColumns("a");

        IndexPosition expectedPos = new IndexPosition(0, 0, "b");

        new DFAsserts(df, expectedPos)
                .expectHeight(2)
                .expectRow(0, "x")
                .expectRow(1, "y");
    }

    @Test
    public void testDropColumns2() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = createDf(i1,
                1, "x",
                2, "y")
                .dropColumns("b");

        new DFAsserts(df, new IndexPosition(0, 0, "a"))
                .expectHeight(2)
                .expectRow(0, 1)
                .expectRow(1, 2);
    }

    @Test
    public void testDropColumns3() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = createDf(i1,
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
        Index i1 = Index.withNames("a", "b");
        DataFrame df = createDf(i1,
                1, "x",
                2, "y")
                .dropColumns("no_such_column");

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y");
    }
}
