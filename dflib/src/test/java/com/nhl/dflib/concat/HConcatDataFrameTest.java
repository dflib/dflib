package com.nhl.dflib.concat;

import com.nhl.dflib.DFAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.join.JoinType;
import com.nhl.dflib.map.RowCombiner;
import com.nhl.dflib.map.RowMapper;
import org.junit.Test;

public class HConcatDataFrameTest {

    @Test
    public void testConstructor() {

        Index i1 = Index.withNames("a");
        DataFrame df1 = DataFrame.fromSequence(i1,
                1,
                2);

        Index i2 = Index.withNames("b");
        DataFrame df2 = DataFrame.fromSequence(i2,
                10,
                20);

        DataFrame df = new HConcatDataFrame(
                Index.withNames("a", "b"),
                JoinType.inner,
                df1,
                df2,
                RowCombiner.zip());

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, 10)
                .expectRow(1, 2, 20);
    }

    @Test
    public void testConstructor_SparseDF() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = DataFrame.fromSequence(i1,
                0, 1,
                2, 3).selectColumns("b");

        Index i2 = Index.withNames("c", "d");
        DataFrame df2 = DataFrame.fromSequence(i2,
                10, 20,
                30, 40).selectColumns("c");

        DataFrame df = new HConcatDataFrame(
                Index.withNames("z1", "z2"),
                JoinType.inner,
                df1,
                df2,
                RowCombiner.zip());

        new DFAsserts(df, "z1", "z2")
                .expectHeight(2)
                .expectRow(0, 1, 10)
                .expectRow(1, 3, 30);
    }

    @Test
    public void testConstructor_SparseDF_ZippedIndex() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = DataFrame.fromSequence(i1,
                0, 1,
                2, 3).selectColumns("b");

        Index i2 = Index.withNames("c", "d");
        DataFrame df2 = DataFrame.fromSequence(i2,
                10, 20,
                30, 40).selectColumns("c");

        DataFrame df = new HConcatDataFrame(
                HConcat.zipIndex(df1.getColumns(), df2.getColumns()),
                JoinType.inner,
                df1,
                df2,
                RowCombiner.zip());

        new DFAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, 10)
                .expectRow(1, 3, 30);
    }

    @Test
    public void testHead() {
        Index i1 = Index.withNames("a");
        DataFrame df1 = DataFrame.fromSequence(i1,
                1,
                2);

        Index i2 = Index.withNames("b");
        DataFrame df2 = DataFrame.fromSequence(i2,
                10,
                20);

        DataFrame df = new HConcatDataFrame(
                Index.withNames("a", "b"),
                JoinType.inner,
                df1,
                df2,
                RowCombiner.zip()).head(1);

        new DFAsserts(df, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1, 10);
    }

    @Test
    public void testRenameColumn() {

        Index i1 = Index.withNames("a");
        DataFrame df1 = DataFrame.fromSequence(i1,
                1,
                2);

        Index i2 = Index.withNames("b");
        DataFrame df2 = DataFrame.fromSequence(i2,
                10,
                20);

        DataFrame df = new HConcatDataFrame(Index.withNames("a", "b"), JoinType.inner, df1, df2, RowCombiner.zip())
                .renameColumn("b", "c");

        new DFAsserts(df, "a", "c")
                .expectHeight(2)
                .expectRow(0, 1, 10)
                .expectRow(1, 2, 20);
    }

    @Test
    public void testMap() {

        Index i1 = Index.withNames("a");
        DataFrame df1 = DataFrame.fromSequence(i1,
                "one",
                "two");

        Index i2 = Index.withNames("b");
        DataFrame df2 = DataFrame.fromSequence(i2,
                1,
                2);

        Index zippedColumns = Index.withNames("x", "y");

        DataFrame df = new HConcatDataFrame(zippedColumns, JoinType.inner, df1, df2, RowCombiner.zip())
                .map(RowMapper.mapColumn("x", (cx, rx) -> cx.get(rx, 0) + "_"));

        new DFAsserts(df, zippedColumns)
                .expectHeight(2)
                .expectRow(0, "one_", 1)
                .expectRow(1, "two_", 2);
    }

    @Test
    public void testMap_ChangeRowStructure() {

        Index i1 = Index.withNames("a");
        DataFrame df1 = DataFrame.fromSequence(i1,
                "one",
                "two");

        Index i2 = Index.withNames("b");
        DataFrame df2 = DataFrame.fromSequence(i2,
                1,
                2);

        Index mappedColumns = Index.withNames("x", "y", "z");
        DataFrame df = new HConcatDataFrame(Index.withNames("a", "b"), JoinType.inner, df1, df2, RowCombiner.zip())
                .map(mappedColumns, (c, sr, tr) -> c
                        .set(tr, 0, sr[0])
                        .set(tr, 1, ((int) sr[1]) * 10)
                        .set(tr, 2, sr[1]));

        new DFAsserts(df, mappedColumns)
                .expectHeight(2)
                .expectRow(0, "one", 10, 1)
                .expectRow(1, "two", 20, 2);
    }
}
