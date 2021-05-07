package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static java.util.Arrays.asList;

@Deprecated
public class DataFrame_AggregatorLegacyTest {

    @Test
    public void testMix() {
        DataFrame df = DataFrame.newFrame("a", "b", "c", "d").foldByRow(
                1, "x", "n", 1.0,
                2, "y", "a", 2.5,
                0, "a", "z", 0.001);

        DataFrame agg = df.agg(
                Aggregator.sumLong("a"),
                Aggregator.countLong(),
                Aggregator.sumDouble("d"));

        new DataFrameAsserts(agg, "a", "_long_count", "d")
                .expectHeight(1)
                .expectRow(0, 3L, 3L, 3.501);
    }

    @Test
    public void test_CountInt_CountLong() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                0, "a");

        DataFrame agg = df.agg(
                Aggregator.countLong(),
                Aggregator.countInt());

        new DataFrameAsserts(agg, "_long_count", "_int_count")
                .expectHeight(1)
                .expectRow(0, 2L, 2);
    }

    @Test
    public void test_SumInt_SumLong() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 1,
                -1, 5L);

        DataFrame agg = df.agg(
                Aggregator.sumInt("a"),
                Aggregator.sumLong(1));

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, 0, 6L);
    }

    @Test
    public void test_MinMax() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 1,
                -1, 1,
                8, 1);

        DataFrame agg = df.agg(
                Aggregator.min("a"),
                Aggregator.max("a"));

        new DataFrameAsserts(agg, "a", "a_")
                .expectHeight(1)
                .expectRow(0, -1, 8);
    }

    @Test
    public void test_Concat() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                0, "a");

        DataFrame agg = df.agg(
                Aggregator.concat("a", "_"),
                Aggregator.concat(1, " ", "[", "]"));

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, "1_0", "[x a]");
    }

    @Test
    public void test_Set() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "x",
                1, "a");

        DataFrame agg = df.agg(Aggregator.set("a"), Aggregator.set(1));

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, new HashSet<>(asList(1, 2)), new HashSet<>(asList("x", "a")));
    }

    @Test
    public void test_list() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "x",
                1, "a");

        DataFrame agg = df.agg(Aggregator.list("a"), Aggregator.list(1));

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, asList(1, 2, 1), asList("x", "x", "a"));
    }

    @Test
    public void test_average() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 4L,
                0, 55.5);

        DataFrame agg = df.agg(
                Aggregator.averageDouble("a"),
                Aggregator.averageDouble(1));

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, 0.5, 29.75);
    }

    @Test
    public void test_median_odd() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 100,
                0, 55.5,
                4, 0);

        DataFrame agg = df.agg(
                Aggregator.medianDouble("a"),
                Aggregator.medianDouble(1));

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1., 55.5);
    }

    @Test
    public void test_median_even() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 100,
                0, 55.5,
                4, 0,
                3, 5);

        DataFrame agg = df.agg(
                Aggregator.medianDouble("a"),
                Aggregator.medianDouble(1));

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, 2., 30.25);
    }

    @Test
    public void test_median_zero() {
        DataFrame df = DataFrame.newFrame("a", "b").empty();

        DataFrame agg = df.agg(
                Aggregator.medianDouble("a"),
                Aggregator.medianDouble(1));

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, 0., 0.);
    }

    @Test
    public void test_median_one() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(1, 100);

        DataFrame agg = df.agg(
                Aggregator.medianDouble("a"),
                Aggregator.medianDouble(1));

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1., 100.);
    }

    @Test
    public void test_median_nulls() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, null,
                0, 55.5,
                4, 0,
                null, 5);

        DataFrame agg = df.agg(
                Aggregator.medianDouble("a"),
                Aggregator.medianDouble(1));

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1., 5.);
    }

    @Test
    public void test_First() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 100,
                2, 5);

        DataFrame agg = df.agg(
                Aggregator.first("a"),
                Aggregator.first(1));

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1, 100);
    }

    @Test
    public void test_First_Empty() {
        DataFrame df = DataFrame.newFrame("a", "b").empty();

        DataFrame agg = df.agg(
                Aggregator.first("a"),
                Aggregator.first(1));

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, null, null);
    }

    @Test
    public void test_First_Nulls() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, null,
                null, 5);

        DataFrame agg = df.agg(
                Aggregator.first("a"),
                Aggregator.first(1));

        new DataFrameAsserts(agg, "a", "b")
                .expectHeight(1)
                .expectRow(0, 1, null);
    }

    @Test
    public void test_Custom() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 100,
                2, 5);

        DataFrame agg = df.agg(Aggregator.of(adf -> adf.height()));

        new DataFrameAsserts(agg, "of")
                .expectHeight(1)
                .expectRow(0, 2);
    }
}
