package com.nhl.dflib;

import com.nhl.dflib.join.JoinIndicator;
import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

@Deprecated
public class DataFrame_Join_Hash_LegacyTest {

    @Test
    public void asRight() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin()
                .on(0)
                .with(df2.as("df2"));

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

        DataFrame df = df1.as("df1").innerJoin()
                .on(0)
                .with(df2);

        new DataFrameAsserts(df, "df1.a", "df1.b", "a", "b")
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

        DataFrame df = df1.as("df1").innerJoin()
                .on(0)
                .with(df2.as("df2"));

        new DataFrameAsserts(df, "df1.a", "df1.b", "df2.a", "df2.b")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, "a")
                .expectRow(1, 2, "y", 2, "b");
    }

    @Test
    public void inner() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                4, "z");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                "a", 2,
                "b", 2,
                "x", 4,
                "c", 3);

        DataFrame df = df1.innerJoin()
                .on(Hasher.of(0), Hasher.of(1))
                .with(df2);

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(3)
                .expectRow(0, 2, "y", "a", 2)
                .expectRow(1, 2, "y", "b", 2)
                .expectRow(2, 4, "z", "x", 4);
    }

    @Test
    public void full_IntColumn() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y")
                .compactInt(0, 0);

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                2, "a",
                2, "b",
                3, "c").compactInt(0, 0);

        DataFrame df = df1.fullJoin()
                .on(0)
                .with(df2);

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(4)
                .expectRow(0, 1, "x", null, null)
                .expectRow(1, 2, "y", 2, "a")
                .expectRow(2, 2, "y", 2, "b")
                .expectRow(3, null, null, 3, "c");
    }


    @Test
    public void inner_Indexed_HashOverlap() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin()
                .on(0)
                .with(df2);

        new DataFrameAsserts(df, "a", "b", "a_", "b_")
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

        DataFrame df = df1.leftJoin()
                .on(0)
                .with(df2);

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(3)
                .expectRow(0, 1, "x", null, null)
                .expectRow(1, 2, "y", 2, "a")
                .expectRow(2, 2, "y", 2, "b");
    }

    @Test
    public void right_ByPos() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df2.rightJoin()
                .on(0)
                .with(df1);

        new DataFrameAsserts(df, "c", "d", "a", "b")
                .expectHeight(3)
                .expectRow(0, null, null, 1, "x")
                .expectRow(1, 2, "a", 2, "y")
                .expectRow(2, 2, "b", 2, "y");
    }

    @Test
    public void right_ByName() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df2.rightJoin()
                .on("c", "a")
                .with(df1);

        new DataFrameAsserts(df, "c", "d", "a", "b")
                .expectHeight(3)
                .expectRow(0, null, null, 1, "x")
                .expectRow(1, 2, "a", 2, "y")
                .expectRow(2, 2, "b", 2, "y");
    }

    @Test
    public void right_ByMatchingName() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "d").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df2.rightJoin()
                .on("a")
                .with(df1);

        new DataFrameAsserts(df, "a", "d", "a_", "b")
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

        DataFrame df = df1.fullJoin()
                .on(0)
                .with(df2);

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(4)
                .expectRow(0, 1, "x", null, null)
                .expectRow(1, 2, "y", 2, "a")
                .expectRow(2, 2, "y", 2, "b")
                .expectRow(3, null, null, 3, "c");
    }

    @Test
    public void multiColumnHash() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "a",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df2.innerJoin()
                .on("c", "a")
                .on("d", "b")
                .with(df1);

        new DataFrameAsserts(df, "c", "d", "a", "b")
                .expectHeight(1)
                .expectRow(0, 2, "a", 2, "a");
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

        DataFrame df = df1.fullJoin()
                .on(0)
                .indicatorColumn("ind")
                .with(df2);

        new DataFrameAsserts(df, "a", "b", "c", "d", "ind")
                .expectHeight(4)
                .expectRow(0, 1, "x", null, null, JoinIndicator.left_only)
                .expectRow(1, 2, "y", 2, "a", JoinIndicator.both)
                .expectRow(2, 2, "y", 2, "b", JoinIndicator.both)
                .expectRow(3, null, null, 3, "c", JoinIndicator.right_only);
    }
}
