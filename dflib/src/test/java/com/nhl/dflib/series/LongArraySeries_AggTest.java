package com.nhl.dflib.series;

import com.nhl.dflib.SeriesAggregator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LongArraySeries_AggTest {

    @Test
    public void testSum() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(3L, s.sum());
    }

    @Test
    public void testAgg_SumInt() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(Integer.valueOf(3), s.agg(SeriesAggregator.sumInt()));
    }

    @Test
    public void testAgg_SumLong() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(Long.valueOf(3), s.agg(SeriesAggregator.sumLong()));
    }

    @Test
    public void testAgg_SumDouble() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(Double.valueOf(3.), s.agg(SeriesAggregator.sumDouble()));
    }

    @Test
    public void testAgg_CountInt() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(Integer.valueOf(2), s.agg(SeriesAggregator.countInt()));
    }

    @Test
    public void testAgg_CountLong() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(Long.valueOf(2), s.agg(SeriesAggregator.countLong()));
    }

    @Test
    public void testMax() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(56L, s.max());
    }

    @Test
    public void testAgg_MaxInt() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(Integer.valueOf(56), s.agg(SeriesAggregator.maxInt()));
    }

    @Test
    public void testAgg_MaxLong() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(Long.valueOf(56), s.agg(SeriesAggregator.maxLong()));
    }

    @Test
    public void testAgg_MaxDouble() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(Double.valueOf(56.), s.agg(SeriesAggregator.maxDouble()));
    }

    @Test
    public void testMin() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2L, s.min());
    }

    @Test
    public void testAgg_MinInt() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(Integer.valueOf(-2), s.agg(SeriesAggregator.minInt()));
    }

    @Test
    public void testAgg_MinLong() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(Long.valueOf(-2), s.agg(SeriesAggregator.minLong()));
    }

    @Test
    public void testAgg_MinDouble() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(Double.valueOf(-2.), s.agg(SeriesAggregator.minDouble()));
    }

    @Test
    public void testAverage() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(13.2, s.average(), 0.000001);
    }

    @Test
    public void testMedian() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(3, s.median(), 0.000001);
    }
}
