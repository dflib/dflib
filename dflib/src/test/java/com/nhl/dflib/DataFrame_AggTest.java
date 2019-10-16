package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

import java.util.HashSet;

import static java.util.Arrays.asList;

public class DataFrame_AggTest {

    @Test
    public void testAgg() {
        DataFrame df = DataFrame.newFrame("a", "b", "c", "d").foldByRow(
                1, "x", "n", 1.0,
                2, "y", "a", 2.5,
                0, "a", "z", 0.001);

        Series<?> s = df.agg(
                Aggregator.sumLong("a"),
                Aggregator.countLong(),
                Aggregator.sumDouble("d"));

        new SeriesAsserts(s).expectData(3L, 3L, 3.501);
    }

    @Test
    public void testAgg_CountInt_CountLong() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                0, "a");

        Series<?> s = df.agg(
                Aggregator.countLong(),
                Aggregator.countInt());

        new SeriesAsserts(s).expectData(2L, 2);
    }

    @Test
    public void testAgg_SumInt_SumLong() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 1,
                -1, 5L);

        Series<?> s = df.agg(
                Aggregator.sumInt("a"),
                Aggregator.sumLong(1));

        new SeriesAsserts(s).expectData(0, 6L);
    }

    @Test
    public void testAgg_MinMax() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 1,
                -1, 1,
                8, 1);

        Series<?> s = df.agg(
                Aggregator.minDouble("a"),
                Aggregator.minLong("a"),
                Aggregator.minInt("a"),
                Aggregator.maxDouble("a"),
                Aggregator.maxLong("a"),
                Aggregator.maxInt("a"));

        new SeriesAsserts(s).expectData(-1., -1L, -1, 8., 8L, 8);
    }

    @Test
    public void testAgg_Concat() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                0, "a");

        Series<?> s = df.agg(
                Aggregator.concat("a", "_"),
                Aggregator.concat(1, " ", "[", "]"));

        new SeriesAsserts(s).expectData("1_0", "[x a]");
    }

    @Test
    public void testAgg_Set() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "x",
                1, "a");

        Series<?> s = df.agg(Aggregator.set("a"), Aggregator.set(1));
        new SeriesAsserts(s).expectData(new HashSet<>(asList(1, 2)), new HashSet<>(asList("x", "a")));
    }

    @Test
    public void testAgg_list() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "x",
                1, "a");

        Series<?> s = df.agg(Aggregator.list("a"), Aggregator.list(1));
        new SeriesAsserts(s).expectData(asList(1, 2, 1), asList("x", "x", "a"));
    }

    @Test
    public void testAgg_average() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 4L,
                0, 55.5);

        Series<?> s = df.agg(
                Aggregator.averageDouble("a"),
                Aggregator.averageDouble(1));

        new SeriesAsserts(s).expectData(0.5, 29.75);
    }

    @Test
    public void testAgg_median_odd() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 100,
                0, 55.5,
                4, 0);

        Series<?> s = df.agg(
                Aggregator.medianDouble("a"),
                Aggregator.medianDouble(1));

        new SeriesAsserts(s).expectData(1., 55.5);
    }

    @Test
    public void testAgg_median_even() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 100,
                0, 55.5,
                4, 0,
                3, 5);

        Series<?> s = df.agg(
                Aggregator.medianDouble("a"),
                Aggregator.medianDouble(1));

        new SeriesAsserts(s).expectData(2., 30.25);
    }

    @Test
    public void testAgg_median_zero() {
        DataFrame df = DataFrame.newFrame("a", "b").empty();

        Series<?> s = df.agg(
                Aggregator.medianDouble("a"),
                Aggregator.medianDouble(1));

        new SeriesAsserts(s).expectData(0., 0.);
    }

    @Test
    public void testAgg_median_one() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(1, 100);

        Series<?> s = df.agg(
                Aggregator.medianDouble("a"),
                Aggregator.medianDouble(1));

        new SeriesAsserts(s).expectData(1., 100.);
    }

    @Test
    public void testAgg_median_nulls() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, null,
                0, 55.5,
                4, 0,
                null, 5);

        Series<?> s = df.agg(
                Aggregator.medianDouble("a"),
                Aggregator.medianDouble(1));

        new SeriesAsserts(s).expectData(1., 5.);
    }

    @Test
    public void testAgg_First() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 100,
                2, 5);

        Series<?> s = df.agg(
                Aggregator.first("a"),
                Aggregator.first(1));

        new SeriesAsserts(s).expectData(1, 100);
    }

    @Test
    public void testAgg_First_Empty() {
        DataFrame df = DataFrame.newFrame("a", "b").empty();

        Series<?> s = df.agg(
                Aggregator.first("a"),
                Aggregator.first(1));

        new SeriesAsserts(s).expectData(null, null);
    }

    @Test
    public void testAgg_First_Nulls() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, null,
                null, 5);

        Series<?> s = df.agg(
                Aggregator.first("a"),
                Aggregator.first(1));

        new SeriesAsserts(s).expectData(1, null);
    }

    @Test
    public void testAgg_Custom() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 100,
                2, 5);

        Series<?> s = df.agg(Aggregator.of(adf -> adf.height()));

        new SeriesAsserts(s).expectData(2);
    }
}
