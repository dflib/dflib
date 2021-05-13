package com.nhl.dflib.series;

import com.nhl.dflib.Exp;
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
        assertEquals(3, s.agg(Exp.$int("").sum()).get(0));
    }

    @Test
    public void testAgg_SumLong() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(3L, s.agg(Exp.$long("").sum()).get(0));
    }

    @Test
    public void testAgg_SumDouble() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(3., s.agg(Exp.$double("").sum()).get(0));
    }

    @Test
    public void testAgg_Count() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(2, s.agg(Exp.count()).get(0));
    }

    @Test
    public void testMax() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(56L, s.max());
    }

    @Test
    public void testAgg_MaxInt() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(56, s.agg(Exp.$int("").max()).get(0));
    }

    @Test
    public void testAgg_MaxLong() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(56L, s.agg(Exp.$long("").max()).get(0));
    }

    @Test
    public void testAgg_MaxDouble() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(56., s.agg(Exp.$double("").max()).get(0));
    }

    @Test
    public void testMin() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2L, s.min());
    }

    @Test
    public void testAgg_MinInt() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2, s.agg(Exp.$int("").min()).get(0));
    }

    @Test
    public void testAgg_MinLong() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2L, s.agg(Exp.$long("").min()).get(0));
    }

    @Test
    public void testAgg_MinDouble() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2., s.agg(Exp.$double("").min()).get(0));
    }

    @Test
    public void testAverage() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(13.2, s.avg(), 0.000001);
    }

    @Test
    public void testMedian() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(3, s.median(), 0.000001);
    }
}
