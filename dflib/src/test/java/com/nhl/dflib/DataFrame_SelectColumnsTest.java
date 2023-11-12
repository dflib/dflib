package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.$col;
import static com.nhl.dflib.Exp.$int;

public class DataFrame_SelectColumnsTest {

    @Test
    public void testByCondition() {
        DataFrame df = DataFrame.newFrame("x1", "b", "x2").foldByRow(
                1, "x", "z",
                2, "y", "a")
                .selectColumns(c -> c.startsWith("x"));

        new DataFrameAsserts(df, "x1", "x2")
                .expectHeight(2)
                .expectRow(0, 1, "z")
                .expectRow(1, 2, "a");
    }

    @Test
    public void test() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .selectColumns("b");

        new DataFrameAsserts(df, "b")
                .expectHeight(2)
                .expectRow(0, "x")
                .expectRow(1, "y");
    }

    @Test
    public void testExp() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .selectColumns($col("b"), $int("a").mul($int("a")));

        new DataFrameAsserts(df, "b", "a * a")
                .expectHeight(2)
                .expectRow(0, "x", 1)
                .expectRow(1, "y", 4);
    }

    @Test
    public void testDuplicateColumn() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .selectColumns("b", "b", "b");

        new DataFrameAsserts(df, "b", "b_", "b__")
                .expectHeight(2)
                .expectRow(0, "x", "x", "x")
                .expectRow(1, "y", "y", "y");
    }


    @Test
    public void testByIndex() {

        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "x", "m",
                2, "y", "n")
                .selectColumns(Index.of("b", "a"));

        new DataFrameAsserts(df, "b", "a")
                .expectHeight(2)
                .expectRow(0, "x", 1)
                .expectRow(1, "y", 2);
    }

    @Test
    public void testWithAsExp(){
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y")
                .selectColumns($col("b"), $int("a").mul($int("a")).as("c"));

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(2)
                .expectRow(0, "x", 1)
                .expectRow(1, "y", 4);
    }
}
