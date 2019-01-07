package com.nhl.dflib.join;

import com.nhl.dflib.DFAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.DataRow;
import com.nhl.dflib.Index;
import org.junit.Test;

import static java.util.Arrays.asList;

public class JoinPredicateTest {

    @Test
    public void testOn_ColumnNames() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")));

        Index i2 = Index.withNames("c", "d");
        DataFrame df2 = DataFrame.create(i2, asList(
                DataRow.row(2, "a"),
                DataRow.row(2, "b"),
                DataRow.row(3, "c")));

        DataFrame df = df1.join(df2, JoinPredicate.on("a", "c"));

        new DFAsserts(df, "a", "b", "c", "d")
                .assertLength(2)
                .assertRow(0, 2, "y", 2, "a")
                .assertRow(1, 2, "y", 2, "b");
    }

    @Test
    public void testOn_ColumnIndexes() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")));

        Index i2 = Index.withNames("c", "d");
        DataFrame df2 = DataFrame.create(i2, asList(
                DataRow.row(2, "a"),
                DataRow.row(2, "b"),
                DataRow.row(3, "c")));

        DataFrame df = df1.join(df2, JoinPredicate.on(0, 0));

        new DFAsserts(df, "a", "b", "c", "d")
                .assertLength(2)
                .assertRow(0, 2, "y", 2, "a")
                .assertRow(1, 2, "y", 2, "b");
    }

    @Test
    public void testAndOn_ColumnNames() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")));

        Index i2 = Index.withNames("c", "d");
        DataFrame df2 = DataFrame.create(i2, asList(
                DataRow.row(2, "a"),
                DataRow.row(2, "y"),
                DataRow.row(3, "c")));

        DataFrame df = df1.join(df2, JoinPredicate.on("a", "c").andOn("b", "d"));

        new DFAsserts(df, "a", "b", "c", "d")
                .assertLength(1)
                .assertRow(0, 2, "y", 2, "y");
    }

    @Test
    public void testAndOn_ColumnIndexes() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")));

        Index i2 = Index.withNames("c", "d");
        DataFrame df2 = DataFrame.create(i2, asList(
                DataRow.row(2, "a"),
                DataRow.row(2, "y"),
                DataRow.row(3, "c")));

        DataFrame df = df1.join(df2, JoinPredicate.on(0, 0).andOn(1, 1));

        new DFAsserts(df, "a", "b", "c", "d")
                .assertLength(1)
                .assertRow(0, 2, "y", 2, "y");
    }
}
