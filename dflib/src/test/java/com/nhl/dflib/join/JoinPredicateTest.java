package com.nhl.dflib.join;

import com.nhl.dflib.unit.DFAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import org.junit.Test;

import static java.util.Arrays.asList;

public class JoinPredicateTest {

    @Test
    public void testOn_ColumnNames() {

        Index i1 = Index.forLabels("a", "b");
        DataFrame df1 = DataFrame.forRows(i1, asList(
                DataFrame.row(1, "x"),
                DataFrame.row(2, "y")));

        Index i2 = Index.forLabels("c", "d");
        DataFrame df2 = DataFrame.forRows(i2, asList(
                DataFrame.row(2, "a"),
                DataFrame.row(2, "b"),
                DataFrame.row(3, "c")));

        DataFrame df = df1.innerJoin(df2, JoinPredicate.on("a", "c"));

        new DFAsserts(df, "a", "b", "c", "d")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, "a")
                .expectRow(1, 2, "y", 2, "b");
    }

    @Test
    public void testOn_ColumnIndexes() {

        Index i1 = Index.forLabels("a", "b");
        DataFrame df1 = DataFrame.forRows(i1, asList(
                DataFrame.row(1, "x"),
                DataFrame.row(2, "y")));

        Index i2 = Index.forLabels("c", "d");
        DataFrame df2 = DataFrame.forRows(i2, asList(
                DataFrame.row(2, "a"),
                DataFrame.row(2, "b"),
                DataFrame.row(3, "c")));

        DataFrame df = df1.innerJoin(df2, JoinPredicate.on(0, 0));

        new DFAsserts(df, "a", "b", "c", "d")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, "a")
                .expectRow(1, 2, "y", 2, "b");
    }

    @Test
    public void testAnd_ColumnNames() {

        Index i1 = Index.forLabels("a", "b");
        DataFrame df1 = DataFrame.forRows(i1, asList(
                DataFrame.row(1, "x"),
                DataFrame.row(2, "y")));

        Index i2 = Index.forLabels("c", "d");
        DataFrame df2 = DataFrame.forRows(i2, asList(
                DataFrame.row(2, "a"),
                DataFrame.row(2, "y"),
                DataFrame.row(3, "c")));

        DataFrame df = df1.innerJoin(df2, JoinPredicate.on("a", "c").and("b", "d"));

        new DFAsserts(df, "a", "b", "c", "d")
                .expectHeight(1)
                .expectRow(0, 2, "y", 2, "y");
    }

    @Test
    public void testAnd_ColumnIndexes() {

        Index i1 = Index.forLabels("a", "b");
        DataFrame df1 = DataFrame.forRows(i1, asList(
                DataFrame.row(1, "x"),
                DataFrame.row(2, "y")));

        Index i2 = Index.forLabels("c", "d");
        DataFrame df2 = DataFrame.forRows(i2, asList(
                DataFrame.row(2, "a"),
                DataFrame.row(2, "y"),
                DataFrame.row(3, "c")));

        DataFrame df = df1.innerJoin(df2, JoinPredicate.on(0, 0).and(1, 1));

        new DFAsserts(df, "a", "b", "c", "d")
                .expectHeight(1)
                .expectRow(0, 2, "y", 2, "y");
    }
}
