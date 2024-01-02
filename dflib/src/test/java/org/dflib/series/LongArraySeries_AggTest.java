package org.dflib.series;

import org.dflib.Exp;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LongArraySeries_AggTest {

    @Test
    public void cumSum() {
        LongArraySeries s = new LongArraySeries(1L, 2L, -5L);
        new SeriesAsserts(s.cumSum()).expectData(1L,3L, -2L);
    }

    @Test
    public void sum() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(3L, s.sum());
    }

    @Test
    public void agg_SumInt() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(3, s.agg(Exp.$int("").sum()).get(0));
    }

    @Test
    public void agg_SumLong() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(3L, s.agg(Exp.$long("").sum()).get(0));
    }

    @Test
    public void agg_SumDouble() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(3., s.agg(Exp.$double("").sum()).get(0));
    }

    @Test
    public void agg_Count() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(2, s.agg(Exp.count()).get(0));
    }

    @Test
    public void max() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(56L, s.max());
    }

    @Test
    public void agg_MaxInt() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(56, s.agg(Exp.$int("").max()).get(0));
    }

    @Test
    public void agg_MaxLong() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(56L, s.agg(Exp.$long("").max()).get(0));
    }

    @Test
    public void agg_MaxDouble() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(56., s.agg(Exp.$double("").max()).get(0));
    }

    @Test
    public void min() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2L, s.min());
    }

    @Test
    public void agg_MinInt() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2, s.agg(Exp.$int("").min()).get(0));
    }

    @Test
    public void agg_MinLong() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2L, s.agg(Exp.$long("").min()).get(0));
    }

    @Test
    public void agg_MinDouble() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2., s.agg(Exp.$double("").min()).get(0));
    }

    @Test
    public void average() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(13.2, s.avg(), 0.000001);
    }

    @Test
    public void median() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(3, s.median(), 0.000001);
    }
}
