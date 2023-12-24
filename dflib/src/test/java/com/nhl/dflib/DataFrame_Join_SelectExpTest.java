package com.nhl.dflib;

import com.nhl.dflib.join.JoinIndicator;
import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.*;

public class DataFrame_Join_SelectExpTest {

    @Test
    public void exp() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                4, "z");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                "a", 2,
                "b", 2,
                "x", 4,
                "c", 3);

        DataFrame df = df1.innerJoin(df2)
                .on(Hasher.of(0), Hasher.of(1))
                .select($col("a").as("a1"), $int("a").mul(2).as("a2"), concat($str("c"), $str("b")));

        new DataFrameAsserts(df, "a1", "a2", "concat(c, b)")
                .expectHeight(3)
                .expectRow(0, 2, 4, "ay")
                .expectRow(1, 2, 4, "by")
                .expectRow(2, 4, 8, "xz");
    }

    @Test
    public void autoNamingRefs() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin(df2)
                .on(0)
                .select($col("a"), $col("b_"));

        new DataFrameAsserts(df, "a", "b_")
                .expectHeight(2)
                .expectRow(0, 2, "a")
                .expectRow(1, 2, "b");
    }

    @Test
    public void unaliasedRefs() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin(df2.as("df2"))
                .on(0)
                .select($col("a"), $col("df2.c"), $col("d"));

        new DataFrameAsserts(df, "a", "df2.c", "d")
                .expectHeight(2)
                .expectRow(0, 2, 2, "a")
                .expectRow(1, 2, 2, "b");
    }

    @Test
    public void rightAlias() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin(df2.as("df2"))
                .on(0)
                .select($col("a"), $col("df2.b"));

        new DataFrameAsserts(df, "a", "df2.b")
                .expectHeight(2)
                .expectRow(0, 2, "a")
                .expectRow(1, 2, "b");
    }

    @Test
    public void leftAlias() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.as("df1").innerJoin(df2)
                .on(0)
                .select($col("df1.a"), $col("b"));

        new DataFrameAsserts(df, "df1.a", "b")
                .expectHeight(2)
                .expectRow(0, 2, "a")
                .expectRow(1, 2, "b");
    }

    @Test
    public void bothAliases() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.as("df1").innerJoin(df2.as("df2"))
                .on(0)
                .select($col("df1.a"), $col("df2.b"));

        new DataFrameAsserts(df, "df1.a", "df2.b")
                .expectHeight(2)
                .expectRow(0, 2, "a")
                .expectRow(1, 2, "b");
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
                .on(0)
                .indicatorColumn("ind")
                .select($col("a"), $col("ind"), $col("d"));

        new DataFrameAsserts(df, "a", "ind", "d")
                .expectHeight(4)
                .expectRow(0, 1, JoinIndicator.left_only, null)
                .expectRow(1, 2, JoinIndicator.both, "a")
                .expectRow(2, 2, JoinIndicator.both, "b")
                .expectRow(3, null, JoinIndicator.right_only, "c");
    }
}
