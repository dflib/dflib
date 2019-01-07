package com.nhl.yadf;

import org.junit.Test;

import static java.util.Arrays.asList;

public class DataFrameTest {

    @Test
    public void testCreate() {

        Index i = Index.withNames("a");
        DataFrame df = DataFrame.create(i, asList(
                new Object[]{1},
                new Object[]{2}));

        new DFAsserts(df, i)
                .assertLength(2)
                .assertRow(0, 1)
                .assertRow(1, 2);
    }

    @Test
    public void testAddColumn() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")))
                .addColumn("c", (c, r) -> ((int) c.get(r, 0)) * 10);

        new DFAsserts(df, "a", "b", "c")
                .assertLength(2)
                .assertRow(0, 1, "x", 10)
                .assertRow(1, 2, "y", 20);
    }

    @Test
    public void testAddColumn_Sparse() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")))
                .selectColumns("a")
                .addColumn("c", (c, r) -> ((int) c.get(r, 0)) * 10);

        new DFAsserts(df, "a", "c")
                .assertLength(2)
                .assertRow(0, 1, 10)
                .assertRow(1, 2, 20);
    }

    @Test
    public void testSelectColumns() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")))
                .selectColumns("b");

        new DFAsserts(df, new IndexPosition(0, 1, "b"))
                .assertLength(2)
                .assertRow(0, "x")
                .assertRow(1, "y");
    }

    @Test
    public void testSelectColumns_DuplicateColumn() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")))
                .selectColumns("b", "b", "b");

        new DFAsserts(df, new IndexPosition(0, 1, "b"), new IndexPosition(1, 1, "b_"), new IndexPosition(2, 1, "b__"))
                .assertLength(2)
                .assertRow(0, "x", "x", "x")
                .assertRow(1, "y", "y", "y");
    }

    @Test
    public void testDropColumns1() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")))
                .dropColumns("a");

        new DFAsserts(df, new IndexPosition(0, 1, "b"))
                .assertLength(2)
                .assertRow(0, "x")
                .assertRow(1, "y");
    }

    @Test
    public void testDropColumns2() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")))
                .dropColumns("b");

        new DFAsserts(df, new IndexPosition(0, 0, "a"))
                .assertLength(2)
                .assertRow(0, 1)
                .assertRow(1, 2);
    }

    @Test
    public void testDropColumns3() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")))
                .dropColumns();

        new DFAsserts(df, "a", "b")
                .assertLength(2)
                .assertRow(0, 1, "x")
                .assertRow(1, 2, "y");
    }

    @Test
    public void testDropColumns4() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")))
                .dropColumns("no_such_column");

        new DFAsserts(df, "a", "b")
                .assertLength(2)
                .assertRow(0, 1, "x")
                .assertRow(1, 2, "y");
    }

    @Test
    public void testMap_SameIndex() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")))
                .map((c, r) -> c.mapColumn(r, "a", (cx, rx) -> ((int) cx.get(rx, "a")) * 10));

        new DFAsserts(df, "a", "b")
                .assertLength(2)
                .assertRow(0, 10, "x")
                .assertRow(1, 20, "y");
    }

    @Test
    public void testMap_SameIndex_Sparse() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")))
                .dropColumns("a")
                .map((c, r) -> c.mapColumn(r, "b", (cx, rx) -> cx.get(rx, "b") + "_"));

        new DFAsserts(df, new IndexPosition(0, 0, "b"))
                .assertLength(2)
                .assertRow(0, "x_")
                .assertRow(1, "y_");
    }

    @Test
    public void testMapColumn() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")))
                .mapColumn("a", (c, r) -> ((int) c.get(r, 0)) * 10);

        new DFAsserts(df, "a", "b")
                .assertLength(2)
                .assertRow(0, 10, "x")
                .assertRow(1, 20, "y");
    }

    @Test
    public void testMapColumn_Sparse() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")))
                .selectColumns("b")
                .mapColumn("b", (c, r) -> c.get(r, 0) + "_");

        new DFAsserts(df, "b")
                .assertLength(2)
                .assertRow(0, "x_")
                .assertRow(1, "y_");
    }
}
