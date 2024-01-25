package org.dflib;

import org.dflib.join.JoinIndicator;
import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataFrame_Join_SelectNamesTest {

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
                .select("a", "b", "c", "d");

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
                .select("a", "c");

        new DataFrameAsserts(df, "a", "c")
                .expectHeight(3)
                .expectRow(0, 2, "a")
                .expectRow(1, 2, "b")
                .expectRow(2, 4, "x");
    }

    @Test
    public void predicate() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                4, "z");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                "a", 2,
                "b", 2,
                "x", 4,
                "c", 3);

        DataFrame df = df1.innerJoin(df2)
                .on(Hasher.of(0), Hasher.of(1))
                .select(c -> !c.endsWith("_"));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 2, "y")
                .expectRow(1, 2, "y")
                .expectRow(2, 4, "z");
    }

    @Test
    public void exceptPredicate() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                4, "z");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                "a", 2,
                "b", 2,
                "x", 4,
                "c", 3);

        DataFrame df = df1.innerJoin(df2)
                .on(Hasher.of(0), Hasher.of(1))
                .selectExcept(c -> c.endsWith("_"));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 2, "y")
                .expectRow(1, 2, "y")
                .expectRow(2, 4, "z");
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
                .selectExcept("a", "c");

        new DataFrameAsserts(df, "b", "d")
                .expectHeight(3)
                .expectRow(0, "y", 2)
                .expectRow(1, "y", 2)
                .expectRow(2, "z", 4);
    }

    @Test
    public void autoNaming() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin(df2)
                .on(0)
                .select("a", "b_");

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
                .select("a", "df2.c", "d");

        new DataFrameAsserts(df, "a", "df2.c", "d")
                .expectHeight(2)
                .expectRow(0, 2, 2, "a")
                .expectRow(1, 2, 2, "b");
    }

    @Test
    public void invalidColRefs() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                2, "a",
                2, "b",
                3, "c");

        assertThrows(IllegalArgumentException.class, () -> df1.innerJoin(df2.as("df2"))
                .on(0)
                .select("a", "df2.c", "X"));
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
                .select("a", "df2.b");

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
                .select("df1.a", "b_");

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
                .select("df1.a", "df2.b", "b", "b_");

        new DataFrameAsserts(df, "df1.a", "df2.b", "b", "b_")
                .expectHeight(2)
                .expectRow(0, 2, "a", "y", "a")
                .expectRow(1, 2, "b", "y", "b");
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
                .select("a", "ind", "d");

        new DataFrameAsserts(df, "a", "ind", "d")
                .expectHeight(4)
                .expectRow(0, 1, JoinIndicator.left_only, null)
                .expectRow(1, 2, JoinIndicator.both, "a")
                .expectRow(2, 2, JoinIndicator.both, "b")
                .expectRow(3, null, JoinIndicator.right_only, "c");
    }
}
