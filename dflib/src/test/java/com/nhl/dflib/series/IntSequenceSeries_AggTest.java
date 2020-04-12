package com.nhl.dflib.series;

import com.nhl.dflib.SeriesAggregator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntSequenceSeries_AggTest {

    @Test
    public void testAgg_SumInt() {
        IntSequenceSeries s = new IntSequenceSeries(1, 3);
        assertEquals(Integer.valueOf(3), s.agg(SeriesAggregator.sumInt()));
    }

    @Test
    public void testSum() {
        IntSequenceSeries s = new IntSequenceSeries(1, 3);
        assertEquals(3, s.sum());
    }

    @Test
    public void testAgg_SumLong() {
        IntSequenceSeries s = new IntSequenceSeries(1, 3);
        assertEquals(Long.valueOf(3), s.agg(SeriesAggregator.sumLong()));
    }

    @Test
    public void testAgg_SumDouble() {
        IntSequenceSeries s = new IntSequenceSeries(1, 3);
        assertEquals(Double.valueOf(3.), s.agg(SeriesAggregator.sumDouble()));
    }

    @Test
    public void testAgg_CountInt() {
        IntSequenceSeries s = new IntSequenceSeries(1, 3);
        assertEquals(Integer.valueOf(2), s.agg(SeriesAggregator.countInt()));
    }

    @Test
    public void testAgg_CountLong() {
        IntSequenceSeries s = new IntSequenceSeries(1, 3);
        assertEquals(Long.valueOf(2), s.agg(SeriesAggregator.countLong()));
    }

    @Test
    public void testAgg_MaxInt() {
        IntSequenceSeries s = new IntSequenceSeries(1, 15);
        assertEquals(Integer.valueOf(14), s.agg(SeriesAggregator.maxInt()));
    }

    @Test
    public void testMax() {
        IntSequenceSeries s = new IntSequenceSeries(1, 15);
        assertEquals(14, s.max());
    }

    @Test
    public void testAggMaxLong() {
        IntSequenceSeries s = new IntSequenceSeries(1, 15);
        assertEquals(Long.valueOf(14), s.agg(SeriesAggregator.maxLong()));
    }

    @Test
    public void testAgg_MaxDouble() {
        IntSequenceSeries s = new IntSequenceSeries(1, 15);
        assertEquals(Double.valueOf(14.), s.agg(SeriesAggregator.maxDouble()));
    }

    @Test
    public void testAgg_MinInt() {
        IntSequenceSeries s = new IntSequenceSeries(-1, 15);
        assertEquals(Integer.valueOf(-1), s.agg(SeriesAggregator.minInt()));
    }

    @Test
    public void testMin() {
        IntSequenceSeries s = new IntSequenceSeries(-1, 15);
        assertEquals(-1, s.min());
    }

    @Test
    public void testAgg_MinLong() {
        IntSequenceSeries s = new IntSequenceSeries(-1, 15);
        assertEquals(Long.valueOf(-1), s.agg(SeriesAggregator.minLong()));
    }

    @Test
    public void testAgg_MinDouble() {
        IntSequenceSeries s = new IntSequenceSeries(-1, 15);
        assertEquals(Double.valueOf(-1.), s.agg(SeriesAggregator.minDouble()));
    }

    @Test
    public void testAverage() {
        IntSequenceSeries s1 = new IntSequenceSeries(-1, 5);
        assertEquals(1.5, s1.average(), 0.000001);

        IntSequenceSeries s2 = new IntSequenceSeries(1, 5);
        assertEquals(2.5, s2.average(), 0.000001);
    }

    @Test
    public void testMedian() {
        IntSequenceSeries s1 = new IntSequenceSeries(-1, 5);
        assertEquals(1.5, s1.median(), 0.000001);

        IntSequenceSeries s2 = new IntSequenceSeries(-1, 4);
        assertEquals(1, s2.median(), 0.000001);
    }
}
