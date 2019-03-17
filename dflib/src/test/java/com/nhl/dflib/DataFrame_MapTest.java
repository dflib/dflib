package com.nhl.dflib;

import com.nhl.dflib.map.RowMapper;
import org.junit.Test;

public class DataFrame_MapTest {

    @Test
    public void testMap_SameIndex() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.fromSequenceFoldByRow(i1,
                1, "x",
                2, "y")
                .map(RowMapper.mapColumn("a", r -> ((int) r.get("a")) * 10));

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }

    @Test
    public void testMap_SameIndex_Sparse() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.fromSequenceFoldByRow(i1,
                1, "x",
                2, "y")
                .dropColumns("a")
                .map(RowMapper.mapColumn("b", r -> r.get("b") + "_"));

        new DFAsserts(df, new IndexPosition(0, 0, "b"))
                .expectHeight(2)
                .expectRow(0, "x_")
                .expectRow(1, "y_");
    }

    @Test
    public void testMapColumn_FromRow() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.fromSequenceFoldByRow(i1,
                1, "x",
                2, "y")
                .mapColumn("a", r -> ((int) r.get(0)) * 10);

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }

    @Test
    public void testMapColumn_FromRow_Sparse() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.fromSequenceFoldByRow(i1,
                1, "x",
                2, "y")
                .selectColumns("b")
                .mapColumn("b", r -> r.get(0) + "_");

        new DFAsserts(df, "b")
                .expectHeight(2)
                .expectRow(0, "x_")
                .expectRow(1, "y_");
    }

    @Test
    public void testMapColumn_FromValue() {
        Index i1 = Index.withNames("a", "b");
        DataFrame df = DataFrame.fromSequenceFoldByRow(i1,
                1, "x",
                2, "y")
                .mapColumnValue("a", v -> ((int) v) * 10);

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }

}
