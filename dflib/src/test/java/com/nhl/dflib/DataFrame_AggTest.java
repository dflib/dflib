package com.nhl.dflib;

import com.nhl.dflib.aggregate.Aggregator;
import org.junit.Test;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class DataFrame_AggTest {

    @Test
    public void testAgg() {
        Index i = Index.withNames("a", "b", "c", "d");
        DataFrame df = DataFrame.fromSequence(i,
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
        DataFrame df = DataFrame.fromSequence(i,
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
        DataFrame df = DataFrame.fromSequence(i,
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
        DataFrame df = DataFrame.fromSequence(i,
                1, "x",
                2, "x",
                1, "a");

        Object[] aggregated = df.agg(Aggregator.set("a"), Aggregator.set(1));
        assertNotNull(aggregated);
        assertArrayEquals(new Object[]{new HashSet<>(asList(1, 2)), new HashSet<>(asList("x", "a"))}, aggregated);
    }

    @Test
    public void testAgg_List() {
        Index i = Index.withNames("a", "b");
        DataFrame df = DataFrame.fromSequence(i,
                1, "x",
                2, "x",
                1, "a");

        Object[] aggregated = df.agg(Aggregator.list("a"), Aggregator.list(1));
        assertNotNull(aggregated);
        assertArrayEquals(new Object[]{asList(1, 2, 1), asList("x", "x", "a")}, aggregated);
    }
}
