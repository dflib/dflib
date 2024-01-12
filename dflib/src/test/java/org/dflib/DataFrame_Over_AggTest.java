package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class DataFrame_Over_AggTest {

    @Test
    public void empty() {
        DataFrame df = DataFrame.empty("a", "b", "c");
        DataFrame r = df.over().agg(Exp.$int("a").sum());
        new DataFrameAsserts(r, "sum(a)").expectHeight(0);
    }

    @Test
    public void test() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame r = df.over().agg(Exp.$int("a").sum());
        new DataFrameAsserts(r, "sum(a)")
                .expectHeight(5)
                .expectRow(0, 5)
                .expectRow(1, 5)
                .expectRow(2, 5)
                .expectRow(3, 5)
                .expectRow(4, 5);
    }

    @Test
    public void partitioned() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame r = df.over().partitioned("a").agg(Exp.$int("a").sum());
        new DataFrameAsserts(r, "sum(a)")
                .expectHeight(5)
                .expectRow(0, 3)
                .expectRow(1, 2)
                .expectRow(2, 3)
                .expectRow(3, 0)
                .expectRow(4, 3);
    }

    @Test
    public void sorted() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame r = df.over()
                .sorted(Exp.$col("b").asc())
                .agg(
                        Exp.$int("a").sum(),
                        Exp.$col("b").first()
                );

        new DataFrameAsserts(r, "sum(a)", "b")
                .expectHeight(5)
                .expectRow(0, 5, "a")
                .expectRow(1, 5, "a")
                .expectRow(2, 5, "a")
                .expectRow(3, 5, "a")
                .expectRow(4, 5, "a");
    }

    @Test
    public void partitioned_Sorted() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        DataFrame r = df.over()
                .partitioned("a")
                .sorted(Exp.$col("b").asc())
                .agg(
                        Exp.$int("a").sum(),
                        Exp.$col("b").first()
                );

        new DataFrameAsserts(r, "sum(a)", "b")
                .expectHeight(5)
                .expectRow(0, 3, "x")
                .expectRow(1, 2, "y")
                .expectRow(2, 3, "x")
                .expectRow(3, 0, "a")
                .expectRow(4, 3, "x");
    }
}