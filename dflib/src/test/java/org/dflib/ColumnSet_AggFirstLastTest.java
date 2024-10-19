package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class ColumnSet_AggFirstLastTest {

    @Test
    public void first() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 100,
                2, 5);

        DataFrame agg = df.cols("A", "B").agg(
                $col("a").first(),
                $col(1).first());

        new DataFrameAsserts(agg, "A", "B")
                .expectHeight(1)
                .expectRow(0, 1, 100);
    }

    @Test
    public void firstEmpty() {
        DataFrame df = DataFrame.empty("a", "b");

        DataFrame agg = df.cols("A", "B").agg(
                $col("a").first(),
                $col(1).first());

        new DataFrameAsserts(agg, "A", "B")
                .expectHeight(1)
                .expectRow(0, null, null);
    }

    @Test
    public void firstNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, null,
                null, 5);

        DataFrame agg = df.cols("A", "B").agg(
                $col("a").first(),
                $col(1).first());

        new DataFrameAsserts(agg, "A", "B")
                .expectHeight(1)
                .expectRow(0, 1, null);
    }

    @Test
    public void firstFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                7, 1,
                -1, 5,
                -4, 5);

        DataFrame agg = df.cols("B", "A").agg(
                $col(1).first($int(0).mod(2).eq(0)),
                $col("a").first($int("b").mod(2).eq(1)));

        new DataFrameAsserts(agg, "B", "A")
                .expectHeight(1)
                .expectRow(0, 5, 7);
    }

    @Test
    public void firstFilteredNoMatches() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                7, 1,
                -1, 5,
                -4, 5);

        DataFrame agg = df.cols("B", "A").agg(
                $col(1).first($val(false).castAsBool()),
                $col("a").first($int("b").mod(2).eq(1)));

        new DataFrameAsserts(agg, "B", "A")
                .expectHeight(1)
                .expectRow(0, null, 7);
    }


    @Test
    public void last() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 100,
                2, 5);

        DataFrame agg = df.cols("A", "B").agg(
                $col("a").last(),
                $col(1).last());

        new DataFrameAsserts(agg, "A", "B")
                .expectHeight(1)
                .expectRow(0, 2, 5);
    }

    @Test
    public void first_div_mul() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                2, 1L,
                6, 7L,
                -3, 5L);

        DataFrame agg = df.cols("A", "B").agg(
                $int(0).first().castAsInt().div(2),
                $long(1).first().castAsLong().mul(2));

        new DataFrameAsserts(agg, "A", "B")
                .expectHeight(1)
                .expectRow(0, 1, 2L);
    }

    @Test
    public void last_div_mul() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                2, 1L,
                6, 7L,
                -3, 5L);

        DataFrame agg = df.cols("A", "B").agg(
                $int(0).last().castAsInt().div(2),
                $long(1).last().castAsLong().mul(2));

        new DataFrameAsserts(agg, "A", "B")
                .expectHeight(1)
                .expectRow(0, -1, 10L);
    }
}
