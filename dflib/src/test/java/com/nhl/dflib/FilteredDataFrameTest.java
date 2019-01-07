package com.nhl.dflib;

import org.junit.Test;

public class FilteredDataFrameTest {

    @Test
    public void testIterator() {

        Index i = Index.withNames("a");

        FilteredDataFrame df = new FilteredDataFrame(
                DataFrame.create(i, DataRow.row(1), DataRow.row(4)),
                (c, r) -> ((int) c.get(r, 0)) > 2);

        new DFAsserts(df, "a")
                .assertLength(1)
                .assertRow(0, 4);
    }

    @Test
    public void testIterator_Empty() {
        Index i = Index.withNames("a");
        FilteredDataFrame df = new FilteredDataFrame(DataFrame.create(i), (c, r) -> ((int) c.get(r, 0)) > 2);
        new DFAsserts(df, "a").assertLength(0);
    }

    @Test
    public void testIterator_NoMatch() {
        Index i = Index.withNames("a");
        FilteredDataFrame df = new FilteredDataFrame(DataFrame.create(i, DataRow.row(1),
                DataRow.row(4)), (c, r) -> ((int) c.get(r, 0)) > 4);

        new DFAsserts(df, "a").assertLength(0);
    }

    @Test
    public void testMap() {

        Index i = Index.withNames("a");
        DataFrame df = new FilteredDataFrame(
                DataFrame.create(i, DataRow.row("one"), DataRow.row("two")),
                (c, r) -> c.get(r, 0).equals("two"))
                .map(i, (c, r) -> c.mapColumn(r, "a", (cx, v) -> v[0] + "_"));

        new DFAsserts(df, i)
                .assertLength(1)
                .assertRow(0, "two_");
    }

    @Test
    public void testRenameColumn() {
        Index i = Index.withNames("a", "b");

        DataFrame df = new FilteredDataFrame(
                DataFrame.create(i, DataRow.row("one", 1), DataRow.row("two", 2)),
                (c, r) -> true).renameColumn("b", "c");

        new DFAsserts(df, "a", "c")
                .assertLength(2)
                .assertRow(0, "one", 1)
                .assertRow(1, "two", 2);
    }
}
