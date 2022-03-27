package com.nhl.dflib.series;

import com.nhl.dflib.Exp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SingleLongValueSeries_AggTest {
    @Test
    public void testSum() {
        LongSingleValueSeries s = new LongSingleValueSeries(1L, 2);
        assertEquals(2L, s.sum());
    }

    @Test
    public void testAgg_SumInt() {
        LongSingleValueSeries s = new LongSingleValueSeries(1L, 2);
        assertEquals(2, s.agg(Exp.$int("").sum()).get(0));
    }

    @Test
    public void testAgg_SumLong() {
        LongSingleValueSeries s = new LongSingleValueSeries(1L, 2);
        assertEquals(2L, s.agg(Exp.$long("").sum()).get(0));
    }

    @Test
    public void testAgg_SumDouble() {
        LongSingleValueSeries s = new LongSingleValueSeries(1L, 2);
        assertEquals(2., s.agg(Exp.$double("").sum()).get(0));
    }

    @Test
    public void testAgg_Count() {
        LongSingleValueSeries s = new LongSingleValueSeries(1L, 2);
        assertEquals(2, s.agg(Exp.count()).get(0));
    }

    @Test
    public void testMax() {
        LongSingleValueSeries s = new LongSingleValueSeries(2L, 2);
        assertEquals(2L, s.max());
    }

    @Test
    public void testAgg_MaxInt() {
        LongSingleValueSeries s = new LongSingleValueSeries(2L, 2);
        assertEquals(2, s.agg(Exp.$int("").max()).get(0));
    }

    @Test
    public void testAgg_MaxLong() {
        LongSingleValueSeries s = new LongSingleValueSeries(2L, 2);
        assertEquals(2L, s.agg(Exp.$long("").max()).get(0));
    }

    @Test
    public void testAgg_MaxDouble() {
        LongSingleValueSeries s = new LongSingleValueSeries(2L, 2);
        assertEquals(2., s.agg(Exp.$double("").max()).get(0));
    }

    @Test
    public void testMin() {
        LongSingleValueSeries s = new LongSingleValueSeries(2L, 2);
        assertEquals(2L, s.min());
    }

    @Test
    public void testAgg_MinInt() {
        LongSingleValueSeries s = new LongSingleValueSeries(2L, 2);
        assertEquals(2, s.agg(Exp.$int("").min()).get(0));
    }

    @Test
    public void testAgg_MinLong() {
        LongSingleValueSeries s = new LongSingleValueSeries(2L, 2);
        assertEquals(2L, s.agg(Exp.$long("").min()).get(0));
    }

    @Test
    public void testAgg_MinDouble() {
        LongSingleValueSeries s = new LongSingleValueSeries(2L, 2);
        assertEquals(2., s.agg(Exp.$double("").min()).get(0));
    }

    @Test
    public void testAverage() {
        LongSingleValueSeries s = new LongSingleValueSeries(2L, 2);
        assertEquals(2L, s.avg(), 0.000001);
    }

    @Test
    public void testMedian() {
        LongSingleValueSeries s = new LongSingleValueSeries(2L, 2);
        assertEquals(2L, s.median(), 0.000001);
    }
}
