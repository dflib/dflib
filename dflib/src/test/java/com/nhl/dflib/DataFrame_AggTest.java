package com.nhl.dflib;

import com.nhl.dflib.aggregate.Aggregator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class DataFrame_AggTest extends BaseDataFrameTest {

    public DataFrame_AggTest(boolean columnar) {
        super(columnar);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return asList(new Object[][]{{false}, {true}});
    }

    @Test
    public void testAgg() {
        Index i = Index.withNames("a", "b", "c", "d");
        DataFrame df = createDf(i,
                1, "x", "n", 1.0,
                2, "y", "a", 2.5,
                0, "a", "z", 0.001);

        Object[] aggregated = df.agg(
                Aggregator.sum("a"),
                Aggregator.count(2),
                Aggregator.sumDouble("d"));

        assertNotNull(aggregated);
        assertArrayEquals(new Object[]{3L, 3L, 3.501}, aggregated);
    }

    @Test
    public void testAgg_Count() {
        Index i = Index.withNames("a", "b");
        DataFrame df = createDf(i,
                1, "x",
                0, "a");

        Object[] aggregated = df.agg(
                Aggregator.count("a"),
                Aggregator.count(1));

        assertNotNull(aggregated);
        assertArrayEquals(new Object[]{2L, 2L}, aggregated);
    }

    @Test
    public void testAgg_Concat() {
        Index i = Index.withNames("a", "b");
        DataFrame df = createDf(i,
                1, "x",
                0, "a");

        Object[] aggregated = df.agg(
                Aggregator.concat("a", "_"),
                Aggregator.concat(1, " ", "[", "]"));

        assertNotNull(aggregated);
        assertArrayEquals(new Object[]{"1_0", "[x a]"}, aggregated);
    }

    @Test
    public void testAgg_Set() {
        Index i = Index.withNames("a", "b");
        DataFrame df = createDf(i,
                1, "x",
                2, "x",
                1, "a");

        Object[] aggregated = df.agg(Aggregator.set("a"), Aggregator.set(1));
        assertNotNull(aggregated);
        assertArrayEquals(new Object[]{new HashSet<>(asList(1, 2)), new HashSet<>(asList("x", "a"))}, aggregated);
    }

    @Test
    public void testAgg_list() {
        Index i = Index.withNames("a", "b");
        DataFrame df = createDf(i,
                1, "x",
                2, "x",
                1, "a");

        Object[] aggregated = df.agg(Aggregator.list("a"), Aggregator.list(1));
        assertNotNull(aggregated);
        assertArrayEquals(new Object[]{asList(1, 2, 1), asList("x", "x", "a")}, aggregated);
    }

    @Test
    public void testAgg_average() {
        Index i = Index.withNames("a", "b");
        DataFrame df = createDf(i,
                1, 4L,
                0, 55.5);

        Object[] aggregated = df.agg(
                Aggregator.average("a"),
                Aggregator.average(1));

        assertNotNull(aggregated);
        assertArrayEquals(new Object[]{0.5, 29.75}, aggregated);
    }

    @Test
    public void testAgg_median_odd() {
        Index i = Index.withNames("a", "b");
        DataFrame df = createDf(i,
                1, 100,
                0, 55.5,
                4, 0);

        Object[] aggregated = df.agg(
                Aggregator.median("a"),
                Aggregator.median(1));

        assertNotNull(aggregated);
        assertArrayEquals(new Object[]{1., 55.5}, aggregated);
    }

    @Test
    public void testAgg_median_even() {
        Index i = Index.withNames("a", "b");
        DataFrame df = createDf(i,
                1, 100,
                0, 55.5,
                4, 0,
                3, 5);

        Object[] aggregated = df.agg(
                Aggregator.median("a"),
                Aggregator.median(1));

        assertNotNull(aggregated);
        assertArrayEquals(new Object[]{2., 30.25}, aggregated);
    }

    @Test
    public void testAgg_median_zero() {
        Index i = Index.withNames("a", "b");
        DataFrame df = createDf(i);

        Object[] aggregated = df.agg(
                Aggregator.median("a"),
                Aggregator.median(1));

        assertNotNull(aggregated);
        assertArrayEquals(new Object[]{0., 0.}, aggregated);
    }

    @Test
    public void testAgg_median_one() {
        Index i = Index.withNames("a", "b");
        DataFrame df = createDf(i, 1, 100);

        Object[] aggregated = df.agg(
                Aggregator.median("a"),
                Aggregator.median(1));

        assertNotNull(aggregated);
        assertArrayEquals(new Object[]{1., 100.}, aggregated);
    }

    @Test
    public void testAgg_median_nulls() {
        Index i = Index.withNames("a", "b");
        DataFrame df = createDf(i,
                1, null,
                0, 55.5,
                4, 0,
                null, 5);

        Object[] aggregated = df.agg(
                Aggregator.median("a"),
                Aggregator.median(1));

        assertNotNull(aggregated);
        assertArrayEquals(new Object[]{1., 5.}, aggregated);
    }

    @Test
    public void testAgg_First() {
        Index i = Index.withNames("a", "b");
        DataFrame df = createDf(i,
                1, 100,
                2, 5);

        Object[] aggregated = df.agg(
                Aggregator.first("a"),
                Aggregator.first(1));

        assertNotNull(aggregated);
        assertArrayEquals(new Object[]{1, 100}, aggregated);
    }

    @Test
    public void testAgg_First_Empty() {
        Index i = Index.withNames("a", "b");
        DataFrame df = createDf(i);

        Object[] aggregated = df.agg(
                Aggregator.first("a"),
                Aggregator.first(1));

        assertNotNull(aggregated);
        assertArrayEquals(new Object[]{null, null}, aggregated);
    }

    @Test
    public void testAgg_First_Nulls() {
        Index i = Index.withNames("a", "b");
        DataFrame df = createDf(i,
                1, null,
                null, 5);

        Object[] aggregated = df.agg(
                Aggregator.first("a"),
                Aggregator.first(1));

        assertNotNull(aggregated);
        assertArrayEquals(new Object[]{1, 5}, aggregated);
    }
}
