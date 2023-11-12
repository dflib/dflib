package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class HasherTest {

    @Test
    public void of_ByName() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin()
                .on(Hasher.of("a"), Hasher.of("a"))
                .with(df2);

        new DataFrameAsserts(df, "a", "b", "a_", "b_")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, "a")
                .expectRow(1, 2, "y", 2, "b");
    }

    @Test
    public void of_ByPos() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin()
                .on(Hasher.of(0), Hasher.of(0))
                .with(df2);

        new DataFrameAsserts(df, "a", "b", "a_", "b_")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, "a")
                .expectRow(1, 2, "y", 2, "b");
    }

    @Test
    public void of_MultiColumn_ByName() {

        DataFrame df1 = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", 5L,
                2, "y", 4L);

        DataFrame df2 = DataFrame.foldByRow("x", "y", "z").of(
                2, "a", 6L,
                2, "y", 4L,
                3, "c", 5L);

        DataFrame df = df1.innerJoin()
                .on(Hasher.of("a").and("b").and("c"), Hasher.of("x").and("y").and("z"))
                .with(df2);

        new DataFrameAsserts(df, "a", "b", "c", "x", "y", "z")
                .expectHeight(1)
                .expectRow(0, 2, "y", 4L, 2, "y", 4L);
    }

    @Test
    public void of_MultiColumn_ByPos() {

        DataFrame df1 = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", 5L,
                2, "y", 4L);

        DataFrame df2 = DataFrame.foldByRow("x", "y", "z").of(
                2, "a", 6L,
                2, "y", 4L,
                3, "c", 5L);

        DataFrame df = df1.innerJoin()
                .on(Hasher.of(0).and(1).and(2), Hasher.of(0).and(1).and(2))
                .with(df2);

        new DataFrameAsserts(df, "a", "b", "c", "x", "y", "z")
                .expectHeight(1)
                .expectRow(0, 2, "y", 4L, 2, "y", 4L);
    }
}
