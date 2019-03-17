package com.nhl.dflib.row;

import com.nhl.dflib.DFAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.map.RowMapper;
import org.junit.Test;

public class FilteredRowDataFrameTest {

    @Test
    public void testIterator() {

        Index i = Index.withNames("a");

        FilteredRowDataFrame df = new FilteredRowDataFrame(
                DataFrame.fromRows(i, DataFrame.row(1), DataFrame.row(4)),
                r -> ((int) r.get(0)) > 2);

        new DFAsserts(df, "a")
                .expectHeight(1)
                .expectRow(0, 4);
    }

    @Test
    public void testIterator_Empty() {
        Index i = Index.withNames("a");
        FilteredRowDataFrame df = new FilteredRowDataFrame(DataFrame.fromRows(i), r -> ((int) r.get(0)) > 2);
        new DFAsserts(df, "a").expectHeight(0);
    }

    @Test
    public void testIterator_NoMatch() {
        Index i = Index.withNames("a");
        FilteredRowDataFrame df = new FilteredRowDataFrame(DataFrame.fromRows(i, DataFrame.row(1),
                DataFrame.row(4)), r -> ((int) r.get(0)) > 4);

        new DFAsserts(df, "a").expectHeight(0);
    }

    @Test
    public void testMap() {

        Index i = Index.withNames("a");
        DataFrame df = new FilteredRowDataFrame(
                DataFrame.fromRows(i, DataFrame.row("one"), DataFrame.row("two")),
                r -> r.get(0).equals("two"))
                .map(i, RowMapper.mapColumn("a", er -> er.get(0) + "_"));

        new DFAsserts(df, i)
                .expectHeight(1)
                .expectRow(0, "two_");
    }

    @Test
    public void testRenameColumn() {
        Index i = Index.withNames("a", "b");

        DataFrame df = new FilteredRowDataFrame(
                DataFrame.fromSequenceFoldByRow(i,
                        "one", 1,
                        "two", 2),
                r -> true).renameColumn("b", "c");

        new DFAsserts(df, "a", "c")
                .expectHeight(2)
                .expectRow(0, "one", 1)
                .expectRow(1, "two", 2);
    }
}
