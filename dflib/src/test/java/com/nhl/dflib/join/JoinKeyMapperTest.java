package com.nhl.dflib.join;

import com.nhl.dflib.DFAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.DataRow;
import com.nhl.dflib.Index;
import org.junit.Test;

import static java.util.Arrays.asList;

public class JoinKeyMapperTest {

    @Test
    public void testKeyColumn_ColumnName() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = DataFrame.fromRowsList(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")));

        Index i2 = Index.withNames("a", "b");
        DataFrame df2 = DataFrame.fromRowsList(i2, asList(
                DataRow.row(2, "a"),
                DataRow.row(2, "b"),
                DataRow.row(3, "c")));

        DataFrame df = df1.innerJoin(df2, JoinKeyMapper.keyColumn("a"), JoinKeyMapper.keyColumn("a"));

        new DFAsserts(df, "a", "b", "a_", "b_")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, "a")
                .expectRow(1, 2, "y", 2, "b");
    }

    @Test
    public void testKeyColumn_ColumnIndex() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = DataFrame.fromRowsList(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")));

        Index i2 = Index.withNames("a", "b");
        DataFrame df2 = DataFrame.fromRowsList(i2, asList(
                DataRow.row(2, "a"),
                DataRow.row(2, "b"),
                DataRow.row(3, "c")));

        DataFrame df = df1.innerJoin(df2, JoinKeyMapper.keyColumn(0), JoinKeyMapper.keyColumn(0));

        new DFAsserts(df, "a", "b", "a_", "b_")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, "a")
                .expectRow(1, 2, "y", 2, "b");
    }

    @Test
    public void testAnd_ColumnName() {

        Index i1 = Index.withNames("a", "b", "c");
        DataFrame df1 = DataFrame.fromRowsList(i1, asList(
                DataRow.row(1, "x", 5L),
                DataRow.row(2, "y", 4L)));

        Index i2 = Index.withNames("x", "y", "z");
        DataFrame df2 = DataFrame.fromRowsList(i2, asList(
                DataRow.row(2, "a", 6L),
                DataRow.row(2, "y", 4L),
                DataRow.row(3, "c", 5L)));

        DataFrame df = df1.innerJoin(df2,
                JoinKeyMapper.keyColumn("a").and("b").and("c"),
                JoinKeyMapper.keyColumn("x").and("y").and("z"));

        new DFAsserts(df, "a", "b", "c", "x", "y", "z")
                .expectHeight(1)
                .expectRow(0, 2, "y", 4L, 2, "y", 4L);
    }

    @Test
    public void testAnd_ColumnIndex() {

        Index i1 = Index.withNames("a", "b", "c");
        DataFrame df1 = DataFrame.fromRowsList(i1, asList(
                DataRow.row(1, "x", 5L),
                DataRow.row(2, "y", 4L)));

        Index i2 = Index.withNames("x", "y", "z");
        DataFrame df2 = DataFrame.fromRowsList(i2, asList(
                DataRow.row(2, "a", 6L),
                DataRow.row(2, "y", 4L),
                DataRow.row(3, "c", 5L)));

        DataFrame df = df1.innerJoin(df2,
                JoinKeyMapper.keyColumn(0).and(1).and(2),
                JoinKeyMapper.keyColumn(0).and(1).and(2));

        new DFAsserts(df, "a", "b", "c", "x", "y", "z")
                .expectHeight(1)
                .expectRow(0, 2, "y", 4L, 2, "y", 4L);
    }
}
