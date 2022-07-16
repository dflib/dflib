package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class DataFrame_Over_AggTest {

    @Test
    public void testEmpty() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").empty();
        DataFrame r = df.over().agg(Exp.$int("a").sum());
        new DataFrameAsserts(r, "sum(a)").expectHeight(0);
    }

    @Test
    public void testDefault() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
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
    public void testPartitioned() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
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
    public void testSorted() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
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
    public void testPartitioned_Sorted() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
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
