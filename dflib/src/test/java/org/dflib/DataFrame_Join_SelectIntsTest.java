package org.dflib;

import org.dflib.join.JoinIndicator;
import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class DataFrame_Join_SelectIntsTest {

    @Test
    public void all() {

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
                .cols(0, 1, 2, 3)
                .select();

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(3)
                .expectRow(0, 2, "y", "a", 2)
                .expectRow(1, 2, "y", "b", 2)
                .expectRow(2, 4, "z", "x", 4);
    }

    @Test
    public void some() {

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
                .cols(0, 2)
                .select();

        new DataFrameAsserts(df, "a", "c")
                .expectHeight(3)
                .expectRow(0, 2, "a")
                .expectRow(1, 2, "b")
                .expectRow(2, 4, "x");
    }

    @Test
    public void except() {

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
                .colsExcept(0, 2)
                .select();

        new DataFrameAsserts(df, "b", "d")
                .expectHeight(3)
                .expectRow(0, "y", 2)
                .expectRow(1, "y", 2)
                .expectRow(2, "z", 4);
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
                .cols(0, 3)
                .select();

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
                .cols(0, 3)
                .select();

        new DataFrameAsserts(df, "df1.a", "b_")
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
                .cols(0, 3)
                .select();

        new DataFrameAsserts(df, "df1.a", "df2.b")
                .expectHeight(2)
                .expectRow(0, 2, "a")
                .expectRow(1, 2, "b");
    }

    @Test
    public void exceptPositions() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                4, "z");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                "a", 2,
                "x", 4,
                "c", 3);

        DataFrame df = df1.innerJoin(df2)
                .on(0, 1)
                .colsExcept(0, 2)
                .select();

        new DataFrameAsserts(df, "b", "d")
                .expectHeight(2)
                .expectRow(0, "y", 2)
                .expectRow(1, "z", 4);
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
                .cols(0, 4, 3)
                .select();

        new DataFrameAsserts(df, "a", "ind", "d")
                .expectHeight(4)
                .expectRow(0, 1, JoinIndicator.left_only, null)
                .expectRow(1, 2, JoinIndicator.both, "a")
                .expectRow(2, 2, JoinIndicator.both, "b")
                .expectRow(3, null, JoinIndicator.right_only, "c");
    }
}
