package com.nhl.dflib;

import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Deprecated
public class SeriesAggregatorLegacyTest {

    @Test
    public void testAggregate_AverageDouble() {
        Series<Double> s = Series.forData(1.4, 5.3, -9.4);
        assertEquals(-0.9, SeriesAggregator.averageDouble().eval(s).get(0).doubleValue(), 0.0000001);
    }

    @Test
    public void testAggregate_Concat() {
        Series<String> s = Series.forData("a", "b", "z", "c");
        assertEquals("abzc", SeriesAggregator.concat("").eval(s).get(0));
        assertEquals("[a|b|z|c]", SeriesAggregator.concat("|", "[", "]").eval(s).get(0));
    }

    @Test
    public void testAggregate_First() {
        Series<String> s = Series.forData("a", "b", "z", "c");
        assertEquals("a", SeriesAggregator.first().eval(s).get(0));
    }

    @Test
    public void testAggregate_List() {
        Series<String> s = Series.forData("a", "b", "z", "c");
        assertEquals(asList("a", "b", "z", "c"), SeriesAggregator.list().eval(s).get(0));
    }

    @Test
    public void testAggregate_Max() {
        Series<Integer> s = Series.forData(4, 5, -9);
        assertEquals(Integer.valueOf(5), SeriesAggregator.<Integer>max().eval(s).get(0));
    }

    @Test
    public void testAggregate_Min() {
        Series<Integer> s = Series.forData(4, 5, -9);
        assertEquals(Integer.valueOf(-9), SeriesAggregator.<Integer>min().eval(s).get(0));
    }

    @Test
    public void testAggregate_MaxDouble() {
        Series<Double> s = Series.forData(1.4, 5.3, -9.4);
        assertEquals(5.3, SeriesAggregator.maxDouble().eval(s).get(0).doubleValue(), 0.0000001);
    }

    @Test
    public void testAggregate_MaxInt() {
        Series<Integer> s = Series.forData(4, 5, -9);
        assertEquals(5, SeriesAggregator.maxInt().eval(s).get(0).intValue());
    }

    @Test
    public void testAggregate_MaxLong() {
        Series<Long> s = Series.forData(4L, 5L, -9L);
        assertEquals(5L, SeriesAggregator.maxLong().eval(s).get(0).longValue());
    }

    @Test
    public void testAggregate_MedianDouble() {
        Series<Double> s = Series.forData(1.4, 5.3, -9.4);
        assertEquals(1.4, SeriesAggregator.medianDouble().eval(s).get(0).doubleValue(), 0.0000001);
    }

    @Test
    public void testAggregate_SumDouble() {
        Series<Double> s = Series.forData(1.4, 5.3, -9.4);
        assertEquals(-2.7, SeriesAggregator.sumDouble().eval(s).get(0).doubleValue(), 0.0000001);
    }

    @Test
    public void testAggregate_Set() {
        Series<String> s = Series.forData("a", "b", "z", "c");
        assertEquals(new HashSet<>(asList("a", "b", "z", "c")), SeriesAggregator.set().eval(s).get(0));
    }
}
