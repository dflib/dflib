package com.nhl.dflib;

import com.nhl.dflib.aggregate.Aggregator;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

import java.util.HashSet;

import static java.util.Arrays.asList;

public class DataFrame_AggTest extends BaseDataFrameTest {

    @Test
    public void testAgg() {
        Index i = Index.forLabels("a", "b", "c", "d");
        DataFrame df = createDf(i,
                1, "x", "n", 1.0,
                2, "y", "a", 2.5,
                0, "a", "z", 0.001);

        Series<?> s = df.agg(
                Aggregator.sum("a"),
                Aggregator.count(2),
                Aggregator.sumDouble("d"));

        new SeriesAsserts(s).expectData(3L, 3L, 3.501);
    }

    @Test
    public void testAgg_Count() {
        Index i = Index.forLabels("a", "b");
        DataFrame df = createDf(i,
                1, "x",
                0, "a");

        Series<?> s = df.agg(
                Aggregator.count("a"),
                Aggregator.count(1));

        new SeriesAsserts(s).expectData(2L, 2L);
    }

    @Test
    public void testAgg_Concat() {
        Index i = Index.forLabels("a", "b");
        DataFrame df = createDf(i,
                1, "x",
                0, "a");

        Series<?> s = df.agg(
                Aggregator.concat("a", "_"),
                Aggregator.concat(1, " ", "[", "]"));

        new SeriesAsserts(s).expectData("1_0", "[x a]");
    }

    @Test
    public void testAgg_Set() {
        Index i = Index.forLabels("a", "b");
        DataFrame df = createDf(i,
                1, "x",
                2, "x",
                1, "a");

        Series<?> s = df.agg(Aggregator.set("a"), Aggregator.set(1));
        new SeriesAsserts(s).expectData(new HashSet<>(asList(1, 2)), new HashSet<>(asList("x", "a")));
    }

    @Test
    public void testAgg_list() {
        Index i = Index.forLabels("a", "b");
        DataFrame df = createDf(i,
                1, "x",
                2, "x",
                1, "a");

        Series<?> s = df.agg(Aggregator.list("a"), Aggregator.list(1));
        new SeriesAsserts(s).expectData(asList(1, 2, 1), asList("x", "x", "a"));
    }

    @Test
    public void testAgg_average() {
        Index i = Index.forLabels("a", "b");
        DataFrame df = createDf(i,
                1, 4L,
                0, 55.5);

        Series<?> s = df.agg(
                Aggregator.average("a"),
                Aggregator.average(1));

        new SeriesAsserts(s).expectData(0.5, 29.75);
    }

    @Test
    public void testAgg_median_odd() {
        Index i = Index.forLabels("a", "b");
        DataFrame df = createDf(i,
                1, 100,
                0, 55.5,
                4, 0);

        Series<?> s = df.agg(
                Aggregator.median("a"),
                Aggregator.median(1));

        new SeriesAsserts(s).expectData(1., 55.5);
    }

    @Test
    public void testAgg_median_even() {
        Index i = Index.forLabels("a", "b");
        DataFrame df = createDf(i,
                1, 100,
                0, 55.5,
                4, 0,
                3, 5);

        Series<?> s = df.agg(
                Aggregator.median("a"),
                Aggregator.median(1));

        new SeriesAsserts(s).expectData(2., 30.25);
    }

    @Test
    public void testAgg_median_zero() {
        Index i = Index.forLabels("a", "b");
        DataFrame df = createDf(i);

        Series<?> s = df.agg(
                Aggregator.median("a"),
                Aggregator.median(1));

        new SeriesAsserts(s).expectData(0., 0.);
    }

    @Test
    public void testAgg_median_one() {
        Index i = Index.forLabels("a", "b");
        DataFrame df = createDf(i, 1, 100);

        Series<?> s = df.agg(
                Aggregator.median("a"),
                Aggregator.median(1));

        new SeriesAsserts(s).expectData(1., 100.);
    }

    @Test
    public void testAgg_median_nulls() {
        Index i = Index.forLabels("a", "b");
        DataFrame df = createDf(i,
                1, null,
                0, 55.5,
                4, 0,
                null, 5);

        Series<?> s = df.agg(
                Aggregator.median("a"),
                Aggregator.median(1));

        new SeriesAsserts(s).expectData(1., 5.);
    }

    @Test
    public void testAgg_First() {
        Index i = Index.forLabels("a", "b");
        DataFrame df = createDf(i,
                1, 100,
                2, 5);

        Series<?> s = df.agg(
                Aggregator.first("a"),
                Aggregator.first(1));

        new SeriesAsserts(s).expectData(1, 100);
    }

    @Test
    public void testAgg_First_Empty() {
        Index i = Index.forLabels("a", "b");
        DataFrame df = createDf(i);

        Series<?> s = df.agg(
                Aggregator.first("a"),
                Aggregator.first(1));

        new SeriesAsserts(s).expectData(null, null);
    }

    @Test
    public void testAgg_First_Nulls() {
        Index i = Index.forLabels("a", "b");
        DataFrame df = createDf(i,
                1, null,
                null, 5);

        Series<?> s = df.agg(
                Aggregator.first("a"),
                Aggregator.first(1));

        new SeriesAsserts(s).expectData(1, 5);
    }
}
