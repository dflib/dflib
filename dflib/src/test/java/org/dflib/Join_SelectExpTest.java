package org.dflib;

import org.dflib.join.JoinIndicator;
import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Join_SelectExpTest {

    @Test
    public void cols_Implicit_All() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                4, "z");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                "a", 2,
                "x", 4,
                "c", 3);

        DataFrame df = df1.innerJoin(df2).on(0, 1)
                .select($col("a").as("a1"), $int("a").mul(2).as("a2"), concat($str("c"), $str("b")));

        new DataFrameAsserts(df, "a1", "a2", "concat(c,b)")
                .expectHeight(2)
                .expectRow(0, 2, 4, "ay")
                .expectRow(1, 4, 8, "xz");
    }

    @Test
    public void cols_ByName() {

        DataFrame df1 = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", "X",
                2, "y", "Y",
                4, "z", "Z");

        DataFrame df2 = DataFrame.foldByRow("a", "b", "d").of(
                "a", 2, 20,
                "x", 4, 40,
                "c", 3, 30);

        DataFrame df = df1.join(df2).on(0, 1)
                .cols("X", "Y")
                .select(
                        concat($str("a_"), $str("b")),
                        $int("a").add($int("d")));

        new DataFrameAsserts(df, "X", "Y")
                .expectHeight(2)
                .expectRow(0, "ay", 22)
                .expectRow(1, "xz", 44);
    }

    @Test
    public void cols_ByName_ExpDifferentLength() {

        DataFrame df1 = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", "X",
                2, "y", "Y",
                4, "z", "Z");

        DataFrame df2 = DataFrame.foldByRow("a", "b", "d").of(
                "a", 2, 20,
                "x", 4, 40,
                "c", 3, 30);

        assertThrows(IllegalArgumentException.class, () -> df1.join(df2).on(0, 1)
                .cols("X")
                .select(
                        concat($str("a_"), $str("b")),
                        $int("a").add($int("d"))));
    }

    @Test
    public void cols_ByPos() {

        DataFrame df1 = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", "X",
                2, "y", "Y",
                4, "z", "Z");

        DataFrame df2 = DataFrame.foldByRow("a", "b", "d").of(
                "a", 2, 20,
                "x", 4, 40,
                "c", 3, 30);

        DataFrame df = df1.join(df2)
                .on(0, 1)
                // position selection fixes the resulting column names
                .cols(1, 7)
                .select(
                        concat($str("a_"), $str("b")),
                        $int("a").add($int("d")));

        new DataFrameAsserts(df, "b", "7")
                .expectHeight(2)
                .expectRow(0, "ay", 22)
                .expectRow(1, "xz", 44);
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
                // select all possible valid aliases
                .select($col("df2.a"), $col("df2.b"), $col("a"), $col("a_"), $col("b"), $col("b_"));

        new DataFrameAsserts(df, "df2.a", "df2.b", "a", "a_", "b", "b_")
                .expectHeight(2)
                .expectRow(0, 2, "a", 2, 2, "y", "a")
                .expectRow(1, 2, "b", 2, 2, "y", "b");
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
                // select all possible valid aliases
                .select($col("df1.a"), $col("df1.b"), $col("a"), $col("a_"), $col("b"), $col("b_"));

        new DataFrameAsserts(df, "df1.a", "df1.b", "a", "a_", "b", "b_")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, 2, "y", "a")
                .expectRow(1, 2, "y", 2, 2, "y", "b");
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
                // select all possible valid aliases
                .select($col("df1.a"), $col("df1.b"), $col("df2.a"), $col("df2.b"), $col("a"), $col("a_"), $col("b"), $col("b_"));

        new DataFrameAsserts(df, "df1.a", "df1.b", "df2.a", "df2.b", "a", "a_", "b", "b_")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, "a", 2, 2, "y", "a")
                .expectRow(1, 2, "y", 2, "b", 2, 2, "y", "b");
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
