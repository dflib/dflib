package org.dflib.series;

import org.dflib.Exp;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LongArraySeries_ReduceTest {

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
    public void reduce_SumInt() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(3, s.reduce(Exp.$int("").sum()));
    }

    @Test
    public void reduce_SumLong() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(3L, s.reduce(Exp.$long("").sum()));
    }

    @Test
    public void reduce_SumDouble() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(3., s.reduce(Exp.$double("").sum()));
    }

    @Test
    public void reduce_Count() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(2, s.reduce(Exp.count()));
    }

    @Test
    public void max() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(56L, s.max());
    }

    @Test
    public void reduce_MaxInt() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(56, s.reduce(Exp.$int("").max()));
    }

    @Test
    public void reduce_MaxLong() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(56L, s.reduce(Exp.$long("").max()));
    }

    @Test
    public void reduce_MaxDouble() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(56., s.reduce(Exp.$double("").max()));
    }

    @Test
    public void min() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2L, s.min());
    }

    @Test
    public void reduce_MinInt() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2, s.reduce(Exp.$int("").min()));
    }

    @Test
    public void reduce_MinLong() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2L, s.reduce(Exp.$long("").min()));
    }

    @Test
    public void reduce_MinDouble() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2., s.reduce(Exp.$double("").min()));
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
