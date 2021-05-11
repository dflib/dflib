package com.nhl.dflib.series;

import com.nhl.dflib.SeriesAggregator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Deprecated
public class DoubleArraySeries_AggLegacyTest {

    @Test
    public void testAgg_SumInt() {
        DoubleArraySeries s = new DoubleArraySeries(1, 2.55);
        assertEquals(Integer.valueOf(3), s.agg(SeriesAggregator.sumInt()).get(0));
    }

    @Test
    public void testAgg_SumLong() {
        DoubleArraySeries s = new DoubleArraySeries(1.1, 2);
        assertEquals(Long.valueOf(3), s.agg(SeriesAggregator.sumLong()).get(0));
    }

    @Test
    public void testAgg_SumDouble() {
        DoubleArraySeries s = new DoubleArraySeries(1.1, 2);
        assertEquals(Double.valueOf(3.1), s.agg(SeriesAggregator.sumDouble()).get(0));
    }

    @Test
    public void testAgg_CountInt() {
        DoubleArraySeries s = new DoubleArraySeries(1.1, 2.2);
        assertEquals(Integer.valueOf(2), s.agg(SeriesAggregator.countInt()).get(0));
    }

    @Test
    public void testAgg_CountLong() {
        DoubleArraySeries s = new DoubleArraySeries(1.1, 2.2);
        assertEquals(Long.valueOf(2), s.agg(SeriesAggregator.countLong()).get(0));
    }

    @Test
    public void testAgg_MaxInt() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2, 3, 56.1, 8);
        assertEquals(Integer.valueOf(56), s.agg(SeriesAggregator.maxInt()).get(0));
    }

    @Test
    public void testAgg_MaxLong() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2, 3, 56.1, 8);
        assertEquals(Long.valueOf(56), s.agg(SeriesAggregator.maxLong()).get(0));
    }

    @Test
    public void testAgg_MaxDouble() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2, 3, 56.1, 8);
        assertEquals(Double.valueOf(56.1), s.agg(SeriesAggregator.maxDouble()).get(0));
    }

    @Test
    public void testAgg_MinInt() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2.9, 3, 56, 8);
        assertEquals(Integer.valueOf(-2), s.agg(SeriesAggregator.minInt()).get(0));
    }

    @Test
    public void testAgg_MinLong() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2.9, 3, 56, 8);
        assertEquals(Long.valueOf(-2), s.agg(SeriesAggregator.minLong()).get(0));
    }

    @Test
    public void testAgg_MinDouble() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2.9, 3, 56, 8);
        assertEquals(Double.valueOf(-2.9), s.agg(SeriesAggregator.minDouble()).get(0));
    }
}
