package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class ColumnSet_AggColTest {

    @Test
    public void agg() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 100,
                2, 5);

        DataFrame agg = df.cols().agg(
                $col("a"),
                $col(1));

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1, 100);
    }

    @Test
    public void agg_ColNames() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 100,
                2, 5);

        DataFrame agg = df.cols("A", "B").agg(
                $col("a"),
                $col(1));

        new DataFrameAsserts(agg, "A", "B")
                .expectHeight(1)
                .expectRow(0, 1, 100);
    }

    @Test
    public void empty() {
        DataFrame df = DataFrame.empty("a", "b");

        DataFrame agg = df.cols("A", "B").agg(
                $col("a"),
                $col(1));

        new DataFrameAsserts(agg, "A", "B")
                .expectHeight(1)
                .expectRow(0, null, null);
    }

    @Test
    public void nulls() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, null,
                null, 5);

        DataFrame agg = df.cols("A", "B").agg(
                $col("a"),
                $col(1));

        new DataFrameAsserts(agg, "A", "B")
                .expectHeight(1)
                .expectRow(0, 1, null);
    }

    @Test
    public void div_mul() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                2, 1L,
                6, 7L,
                -3, 5L);

        DataFrame agg = df.cols("A", "B").agg(
                $int(0).div(2),
                $long(1).mul(2));

        new DataFrameAsserts(agg, "A", "B")
                .expectHeight(1)
                .expectRow(0, 1, 2L);
    }
}
