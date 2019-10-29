package com.nhl.dflib.series;

import com.nhl.dflib.SeriesAggregator;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class DoubleArraySeries_AggTest {

    @Test
    public void testSum() {
        DoubleArraySeries s = new DoubleArraySeries(1.1, 2);
        assertEquals(3.1, s.sum(), 0.0000001);
    }

    @Test
    public void testAgg_SumInt() {
        DoubleArraySeries s = new DoubleArraySeries(1, 2.55);
        Assert.assertEquals(Integer.valueOf(3), s.agg(SeriesAggregator.sumInt()));
    }

    @Test
    public void testAgg_SumLong() {
        DoubleArraySeries s = new DoubleArraySeries(1.1, 2);
        assertEquals(Long.valueOf(3), s.agg(SeriesAggregator.sumLong()));
    }

    @Test
    public void testAgg_SumDouble() {
        DoubleArraySeries s = new DoubleArraySeries(1.1, 2);
        assertEquals(Double.valueOf(3.1), s.agg(SeriesAggregator.sumDouble()));
    }

    @Test
    public void testAgg_CountInt() {
        DoubleArraySeries s = new DoubleArraySeries(1.1, 2.2);
        assertEquals(Integer.valueOf(2), s.agg(SeriesAggregator.countInt()));
    }

    @Test
    public void testAgg_CountLong() {
        DoubleArraySeries s = new DoubleArraySeries(1.1, 2.2);
        assertEquals(Long.valueOf(2), s.agg(SeriesAggregator.countLong()));
    }

    @Test
    public void testMax() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2, 3, 56.1, 8);
        assertEquals(56.1, s.max(), 0.0000001);
    }

    @Test
    public void testAgg_MaxInt() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2, 3, 56.1, 8);
        assertEquals(Integer.valueOf(56), s.agg(SeriesAggregator.maxInt()));
    }

    @Test
    public void testAgg_MaxLong() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2, 3, 56.1, 8);
        assertEquals(Long.valueOf(56), s.agg(SeriesAggregator.maxLong()));
    }

    @Test
    public void testAgg_MaxDouble() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2, 3, 56.1, 8);
        assertEquals(Double.valueOf(56.1), s.agg(SeriesAggregator.maxDouble()));
    }

    @Test
    public void testMin() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2.9, 3, 56, 8);
        assertEquals(-2.9, s.min(), 0.00000001);
    }

    @Test
    public void testAgg_MinInt() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2.9, 3, 56, 8);
        assertEquals(Integer.valueOf(-2), s.agg(SeriesAggregator.minInt()));
    }

    @Test
    public void testAgg_MinLong() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2.9, 3, 56, 8);
        assertEquals(Long.valueOf(-2), s.agg(SeriesAggregator.minLong()));
    }

    @Test
    public void testAgg_MinDouble() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2.9, 3, 56, 8);
        assertEquals(Double.valueOf(-2.9), s.agg(SeriesAggregator.minDouble()));
    }

    @Test
    public void testAverage() {
        DoubleArraySeries s = new DoubleArraySeries(1.5, -2.1, 3.7, 56.6, 8.8);
        assertEquals(13.7, s.average(), 0.0000001);
    }

    @Test
    public void testMedian() {
        DoubleArraySeries s = new DoubleArraySeries(1.5, -2.1, 3.7, 56.6, 8.8);
        assertEquals(3.7, s.median(), 0.0000001);
    }
}
