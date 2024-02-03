package org.dflib;

import org.dflib.join.JoinIndicator;
import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.Objects;

public class DataFrame_Join_NestedLoopTest {

    @Test
    public void inner() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin(df2)
                .predicatedBy((lr, rr) -> Objects.equals(lr.get(0), rr.get(0)))
                .select();

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, "a")
                .expectRow(1, 2, "y", 2, "b");
    }

    @Test
    public void inner_NoMatches() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin(df2)
                .predicatedBy((lr, rr) -> false)
                .select();

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(0);
    }

    @Test
    public void inner_IndexOverlap() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin(df2)
                .predicatedBy((lr, rr) -> Objects.equals(lr.get(0), rr.get(0)))
                .select();

        new DataFrameAsserts(df, "a", "b", "a_", "b_")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, "a")
                .expectRow(1, 2, "y", 2, "b");
    }

    @Test
    public void asRight() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin(df2.as("df2"))
                .predicatedBy((lr, rr) -> Objects.equals(lr.get(0), rr.get(0)))
                .select();

        new DataFrameAsserts(df, "a", "b", "df2.a", "df2.b")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, "a")
                .expectRow(1, 2, "y", 2, "b");
    }

    @Test
    public void asLeft() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.as("df1").join(df2)
                .predicatedBy((lr, rr) -> Objects.equals(lr.get(0), rr.get(0)))
                .select();

        new DataFrameAsserts(df, "df1.a", "df1.b", "a_", "b_")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, "a")
                .expectRow(1, 2, "y", 2, "b");
    }

    @Test
    public void asBoth() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.as("df1").innerJoin(df2.as("df2"))
                .predicatedBy((lr, rr) -> Objects.equals(lr.get(0), rr.get(0)))
                .select();

        new DataFrameAsserts(df, "df1.a", "df1.b", "df2.a", "df2.b")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, "a")
                .expectRow(1, 2, "y", 2, "b");
    }

    @Test
    public void left() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1
                .leftJoin(df2)
                .predicatedBy((lr, rr) -> Objects.equals(lr.get(0), rr.get(0)))
                .select();

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(3)
                .expectRow(0, 1, "x", null, null)
                .expectRow(1, 2, "y", 2, "a")
                .expectRow(2, 2, "y", 2, "b");
    }

    @Test
    public void right() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df2
                .rightJoin(df1)
                .predicatedBy((lr, rr) -> Objects.equals(lr.get(0), rr.get(0)))
                .select();

        new DataFrameAsserts(df, "c", "d", "a", "b")
                .expectHeight(3)
                .expectRow(0, null, null, 1, "x")
                .expectRow(1, 2, "a", 2, "y")
                .expectRow(2, 2, "b", 2, "y");
    }

    @Test
    public void full() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1
                .fullJoin(df2)
                .predicatedBy((lr, rr) -> Objects.equals(lr.get(0), rr.get(0)))
                .select();

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(4)
                .expectRow(0, 1, "x", null, null)
                .expectRow(1, 2, "y", 2, "a")
                .expectRow(2, 2, "y", 2, "b")
                .expectRow(3, null, null, 3, "c");
    }

    @Test
    public void indicator() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.fullJoin(df2)
                .predicatedBy((lr, rr) -> Objects.equals(lr.get(0), rr.get(0)))
                .indicatorColumn("ind")
                .select();

        new DataFrameAsserts(df, "a", "b", "c", "d", "ind")
                .expectHeight(4)
                .expectRow(0, 1, "x", null, null, JoinIndicator.left_only)
                .expectRow(1, 2, "y", 2, "a", JoinIndicator.both)
                .expectRow(2, 2, "y", 2, "b", JoinIndicator.both)
                .expectRow(3, null, null, 3, "c", JoinIndicator.right_only);
    }
}
