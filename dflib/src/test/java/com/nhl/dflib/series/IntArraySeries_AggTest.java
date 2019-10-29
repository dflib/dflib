package com.nhl.dflib.series;

import com.nhl.dflib.SeriesAggregator;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class IntArraySeries_AggTest {

    @Test
    public void testAgg_SumInt() {
        IntArraySeries s = new IntArraySeries(1, 2);
        Assert.assertEquals(Integer.valueOf(3), s.agg(SeriesAggregator.sumInt()));
    }

    @Test
    public void testSum() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(3, s.sum());
    }

    @Test
    public void testAgg_SumLong() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(Long.valueOf(3), s.agg(SeriesAggregator.sumLong()));
    }

    @Test
    public void testAgg_SumDouble() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(Double.valueOf(3.), s.agg(SeriesAggregator.sumDouble()));
    }

    @Test
    public void testAgg_CountInt() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(Integer.valueOf(2), s.agg(SeriesAggregator.countInt()));
    }

    @Test
    public void testAgg_CountLong() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(Long.valueOf(2), s.agg(SeriesAggregator.countLong()));
    }

    @Test
    public void testAgg_MaxInt() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(Integer.valueOf(56), s.agg(SeriesAggregator.maxInt()));
    }

    @Test
    public void testMax() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(56, s.max());
    }

    @Test
    public void testAggMaxLong() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(Long.valueOf(56), s.agg(SeriesAggregator.maxLong()));
    }

    @Test
    public void testAgg_MaxDouble() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(Double.valueOf(56.), s.agg(SeriesAggregator.maxDouble()));
    }

    @Test
    public void testAgg_MinInt() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(Integer.valueOf(-2), s.agg(SeriesAggregator.minInt()));
    }

    @Test
    public void testMin() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2, s.min());
    }

    @Test
    public void testAgg_MinLong() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(Long.valueOf(-2), s.agg(SeriesAggregator.minLong()));
    }

    @Test
    public void testAgg_MinDouble() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(Double.valueOf(-2.), s.agg(SeriesAggregator.minDouble()));
    }

    @Test
    public void testAverage() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(13.2, s.average(), 0.000001);
    }

    @Test
    public void testMedian() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(3, s.median(), 0.000001);
    }
}
