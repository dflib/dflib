package com.nhl.dflib.filter;

import com.nhl.dflib.DFAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.map.RowMapper;
import org.junit.Test;

public class FilteredDataFrameTest {

    @Test
    public void testIterator() {

        Index i = Index.withNames("a");

        FilteredDataFrame df = new FilteredDataFrame(
                DataFrame.fromRows(i, DataFrame.row(1), DataFrame.row(4)),
                (c, r) -> ((int) c.get(r, 0)) > 2);

        new DFAsserts(df, "a")
                .expectHeight(1)
                .expectRow(0, 4);
    }

    @Test
    public void testIterator_Empty() {
        Index i = Index.withNames("a");
        FilteredDataFrame df = new FilteredDataFrame(DataFrame.fromRows(i), (c, r) -> ((int) c.get(r, 0)) > 2);
        new DFAsserts(df, "a").expectHeight(0);
    }

    @Test
    public void testIterator_NoMatch() {
        Index i = Index.withNames("a");
        FilteredDataFrame df = new FilteredDataFrame(DataFrame.fromRows(i, DataFrame.row(1),
                DataFrame.row(4)), (c, r) -> ((int) c.get(r, 0)) > 4);

        new DFAsserts(df, "a").expectHeight(0);
    }

    @Test
    public void testMap() {

        Index i = Index.withNames("a");
        DataFrame df = new FilteredDataFrame(
                DataFrame.fromRows(i, DataFrame.row("one"), DataFrame.row("two")),
                (c, r) -> c.get(r, 0).equals("two"))
                .map(i, RowMapper.columnMapper("a", (cx, v) -> v[0] + "_"));

        new DFAsserts(df, i)
                .expectHeight(1)
                .expectRow(0, "two_");
    }

    @Test
    public void testRenameColumn() {
        Index i = Index.withNames("a", "b");

        DataFrame df = new FilteredDataFrame(
                DataFrame.fromRows(i, DataFrame.row("one", 1), DataFrame.row("two", 2)),
                (c, r) -> true).renameColumn("b", "c");

        new DFAsserts(df, "a", "c")
                .expectHeight(2)
                .expectRow(0, "one", 1)
                .expectRow(1, "two", 2);
    }
}
