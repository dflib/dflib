package com.nhl.dflib;

import org.junit.Test;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class SeriesAggregatorTest {

    @Test
    public void testAggregate_AverageDouble() {
        Series<Double> s = Series.forData(1.4, 5.3, -9.4);
        assertEquals(-0.9, SeriesAggregator.averageDouble().aggregate(s).doubleValue(), 0.0000001);
    }

    @Test
    public void testAggregate_Concat() {
        Series<String> s = Series.forData("a", "b", "z", "c");
        assertEquals("abzc", SeriesAggregator.concat("").aggregate(s));
        assertEquals("[a|b|z|c]", SeriesAggregator.concat("|", "[", "]").aggregate(s));
    }

    @Test
    public void testAggregate_First() {
        Series<String> s = Series.forData("a", "b", "z", "c");
        assertEquals("a", SeriesAggregator.first().aggregate(s));
    }

    @Test
    public void testAggregate_List() {
        Series<String> s = Series.forData("a", "b", "z", "c");
        assertEquals(asList("a", "b", "z", "c"), SeriesAggregator.list().aggregate(s));
    }

    @Test
    public void testAggregate_Max() {
        Series<Integer> s = Series.forData(4, 5, -9);
        assertEquals(Integer.valueOf(5), SeriesAggregator.<Integer>max().aggregate(s));
    }

    @Test
    public void testAggregate_Min() {
        Series<Integer> s = Series.forData(4, 5, -9);
        assertEquals(Integer.valueOf(-9), SeriesAggregator.<Integer>min().aggregate(s));
    }

    @Test
    public void testAggregate_MaxDouble() {
        Series<Double> s = Series.forData(1.4, 5.3, -9.4);
        assertEquals(5.3, SeriesAggregator.maxDouble().aggregate(s).doubleValue(), 0.0000001);
    }

    @Test
    public void testAggregate_MaxInt() {
        Series<Integer> s = Series.forData(4, 5, -9);
        assertEquals(5, SeriesAggregator.maxInt().aggregate(s).intValue());
    }

    @Test
    public void testAggregate_MaxLong() {
        Series<Long> s = Series.forData(4L, 5L, -9L);
        assertEquals(5L, SeriesAggregator.maxLong().aggregate(s).longValue());
    }

    @Test
    public void testAggregate_MedianDouble() {
        Series<Double> s = Series.forData(1.4, 5.3, -9.4);
        assertEquals(1.4, SeriesAggregator.medianDouble().aggregate(s).doubleValue(), 0.0000001);
    }

    @Test
    public void testAggregate_SumDouble() {
        Series<Double> s = Series.forData(1.4, 5.3, -9.4);
        assertEquals(-2.7, SeriesAggregator.sumDouble().aggregate(s).doubleValue(), 0.0000001);
    }

    @Test
    public void testAggregate_Set() {
        Series<String> s = Series.forData("a", "b", "z", "c");
        assertEquals(new HashSet<>(asList("a", "b", "z", "c")),
                SeriesAggregator.set().aggregate(s));
    }
}
