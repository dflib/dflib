package com.nhl.yadf;

import com.nhl.yadf.join.IndexedJoiner;
import com.nhl.yadf.join.JoinSemantics;
import com.nhl.yadf.join.Joiner;
import org.junit.Test;

import java.util.Objects;

import static java.util.Arrays.asList;

public class DataFrameJoinsTest {

    @Test
    public void testJoin_Inner() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")));

        Index i2 = Index.withNames("c", "d");
        DataFrame df2 = DataFrame.create(i2, asList(
                DataRow.row(2, "a"),
                DataRow.row(2, "b"),
                DataRow.row(3, "c")));

        DataFrame df = df1.join(df2, (lr, rr) -> Objects.equals(lr[0], rr[0]));

        new DFAsserts(df, "a", "b", "c", "d")
                .assertLength(2)
                .assertRow(0, 2, "y", 2, "a")
                .assertRow(1, 2, "y", 2, "b");
    }

    @Test
    public void testJoin_NoMatches() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")));

        Index i2 = Index.withNames("c", "d");
        DataFrame df2 = DataFrame.create(i2, asList(
                DataRow.row(2, "a"),
                DataRow.row(2, "b"),
                DataRow.row(3, "c")));

        DataFrame df = df1.join(df2, (lr, rr) -> false);

        new DFAsserts(df, "a", "b", "c", "d")
                .assertLength(0);
    }

    @Test
    public void testJoin_IndexOverlap() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")));

        Index i2 = Index.withNames("a", "b");
        DataFrame df2 = DataFrame.create(i2, asList(
                DataRow.row(2, "a"),
                DataRow.row(2, "b"),
                DataRow.row(3, "c")));

        DataFrame df = df1.join(df2, (lr, rr) -> Objects.equals(lr[0], rr[0]));

        new DFAsserts(df, "a", "b", "a_", "b_")
                .assertLength(2)
                .assertRow(0, 2, "y", 2, "a")
                .assertRow(1, 2, "y", 2, "b");
    }

    @Test
    public void testJoin_Left() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")));

        Index i2 = Index.withNames("c", "d");
        DataFrame df2 = DataFrame.create(i2, asList(
                DataRow.row(2, "a"),
                DataRow.row(2, "b"),
                DataRow.row(3, "c")));

        Joiner joiner = new Joiner((lr, rr) -> Objects.equals(lr[0], rr[0]), JoinSemantics.left);
        DataFrame df = df1.join(df2, joiner);

        new DFAsserts(df, "a", "b", "c", "d")
                .assertLength(3)
                .assertRow(0, 1, "x", null, null)
                .assertRow(1, 2, "y", 2, "a")
                .assertRow(2, 2, "y", 2, "b");
    }

    @Test
    public void testJoin_Right() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")));

        Index i2 = Index.withNames("c", "d");
        DataFrame df2 = DataFrame.create(i2, asList(
                DataRow.row(2, "a"),
                DataRow.row(2, "b"),
                DataRow.row(3, "c")));

        Joiner joiner = new Joiner((lr, rr) -> Objects.equals(lr[0], rr[0]), JoinSemantics.right);
        DataFrame df = df2.join(df1, joiner);

        new DFAsserts(df, "c", "d", "a", "b")
                .assertLength(3)
                .assertRow(0, null, null, 1, "x")
                .assertRow(1, 2, "a", 2, "y")
                .assertRow(2, 2, "b", 2, "y");
    }

    @Test
    public void testJoin_Full() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")));

        Index i2 = Index.withNames("c", "d");
        DataFrame df2 = DataFrame.create(i2, asList(
                DataRow.row(2, "a"),
                DataRow.row(2, "b"),
                DataRow.row(3, "c")));

        Joiner joiner = new Joiner((lr, rr) -> Objects.equals(lr[0], rr[0]), JoinSemantics.full);
        DataFrame df = df1.join(df2, joiner);

        new DFAsserts(df, "a", "b", "c", "d")
                .assertLength(4)
                .assertRow(0, 1, "x", null, null)
                .assertRow(1, 2, "y", 2, "a")
                .assertRow(2, 2, "y", 2, "b")
                .assertRow(3, null, null, 3, "c");
    }

    @Test
    public void testJoin_InnerIndexed() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")));

        Index i2 = Index.withNames("c", "d");
        DataFrame df2 = DataFrame.create(i2, asList(
                DataRow.row("a", 2),
                DataRow.row("b", 2),
                DataRow.row("c", 3)));

        IndexedJoiner<Object> joiner = new IndexedJoiner<>((c, r) -> r[0], (c, r) -> r[1], JoinSemantics.inner);
        DataFrame df = df1.join(df2, joiner);

        new DFAsserts(df, "a", "b", "c", "d")
                .assertLength(2)
                .assertRow(0, 2, "y", "a", 2)
                .assertRow(1, 2, "y", "b", 2);
    }

    @Test
    public void testJoin_Indexed_IndexOverlap() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")));

        Index i2 = Index.withNames("a", "b");
        DataFrame df2 = DataFrame.create(i2, asList(
                DataRow.row(2, "a"),
                DataRow.row(2, "b"),
                DataRow.row(3, "c")));

        IndexedJoiner<Object> joiner = new IndexedJoiner<>((c, r) -> r[0], (c, r) -> r[0], JoinSemantics.inner);
        DataFrame df = df1.join(df2, joiner);

        new DFAsserts(df, "a", "b", "a_", "b_")
                .assertLength(2)
                .assertRow(0, 2, "y", 2, "a")
                .assertRow(1, 2, "y", 2, "b");
    }

    @Test
    public void testJoin_LeftIndexed() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")));

        Index i2 = Index.withNames("c", "d");
        DataFrame df2 = DataFrame.create(i2, asList(
                DataRow.row(2, "a"),
                DataRow.row(2, "b"),
                DataRow.row(3, "c")));

        IndexedJoiner<Object> joiner = new IndexedJoiner<>((c, r) -> r[0], (c, r) -> r[0], JoinSemantics.left);
        DataFrame df = df1.join(df2, joiner);

        new DFAsserts(df, "a", "b", "c", "d")
                .assertLength(3)
                .assertRow(0, 1, "x", null, null)
                .assertRow(1, 2, "y", 2, "a")
                .assertRow(2, 2, "y", 2, "b");
    }

    @Test
    public void testJoin_RightIndexed() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")));

        Index i2 = Index.withNames("c", "d");
        DataFrame df2 = DataFrame.create(i2, asList(
                DataRow.row(2, "a"),
                DataRow.row(2, "b"),
                DataRow.row(3, "c")));

        IndexedJoiner<Object> joiner = new IndexedJoiner<>((c, r) -> r[0], (c, r) -> r[0], JoinSemantics.right);
        DataFrame df = df2.join(df1, joiner);

        new DFAsserts(df, "c", "d", "a", "b")
                .assertLength(3)
                .assertRow(0, null, null, 1, "x")
                .assertRow(1, 2, "a", 2, "y")
                .assertRow(2, 2, "b", 2, "y");
    }

    @Test
    public void testJoin_FullIndexed() {

        Index i1 = Index.withNames("a", "b");
        DataFrame df1 = DataFrame.create(i1, asList(
                DataRow.row(1, "x"),
                DataRow.row(2, "y")));

        Index i2 = Index.withNames("c", "d");
        DataFrame df2 = DataFrame.create(i2, asList(
                DataRow.row(2, "a"),
                DataRow.row(2, "b"),
                DataRow.row(3, "c")));

        IndexedJoiner<Object> joiner = new IndexedJoiner<>((c, r) -> r[0], (c, r) -> r[0], JoinSemantics.full);
        DataFrame df = df1.join(df2, joiner);

        new DFAsserts(df, "a", "b", "c", "d")
                .assertLength(4)
                .assertRow(0, 1, "x", null, null)
                .assertRow(1, 2, "y", 2, "a")
                .assertRow(2, 2, "y", 2, "b")
                .assertRow(3, null, null, 3, "c");
    }

}
