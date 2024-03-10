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

        DataFrame agg = df.cols().agg(
                $col("a").first(),
                $col(1).first());

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1, 100);
    }

    @Test
    public void firstEmpty() {
        DataFrame df = DataFrame.empty("a", "b");

        DataFrame agg = df.cols().agg(
                $col("a").first(),
                $col(1).first());

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, null, null);
    }

    @Test
    public void firstNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, null,
                null, 5);

        DataFrame agg = df.cols().agg(
                $col("a").first(),
                $col(1).first());

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1, null);
    }

    @Test
    public void firstFiltered() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                7, 1,
                -1, 5,
                -4, 5);

        DataFrame agg = df.cols().agg(
                $col(1).first($int(0).mod(2).eq(0)),
                $col("a").first($int("b").mod(2).eq(1)));

        new DataFrameAsserts(agg, "b", "a")
                .expectHeight(1)
                .expectRow(0, 5, 7);
    }

    @Test
    public void firstFilteredNoMatches() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                7, 1,
                -1, 5,
                -4, 5);

        DataFrame agg = df.cols().agg(
                $col(1).first($val(false).castAsBool()),
                $col("a").first($int("b").mod(2).eq(1)));

        new DataFrameAsserts(agg, "b", "a")
                .expectHeight(1)
                .expectRow(0, null, 7);
    }


    @Test
    public void last() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 100,
                2, 5);

        DataFrame agg = df.cols().agg(
                $col("a").last(),
                $col(1).last());

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, 2, 5);
    }
}
