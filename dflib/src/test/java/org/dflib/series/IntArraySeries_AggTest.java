package org.dflib.series;

import org.dflib.Exp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class IntArraySeries_AggTest {

    @Test
    public void first() {
        IntArraySeries s1 = new IntArraySeries(1, 2);
        assertEquals(Integer.valueOf(1), s1.first());

        IntArraySeries s2 = new IntArraySeries();
        assertNull(s2.first());
    }

    @Test
    public void sum() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(3, s.sum());
    }

    @Test
    public void agg_SumInt() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(3, s.agg(Exp.$int("").sum()).get(0));
    }

    @Test
    public void agg_SumLong() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(3L, s.agg(Exp.$long("").sum()).get(0));
    }

    @Test
    public void agg_SumDouble() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(3., (Double) s.agg(Exp.$double("").sum()).get(0), 0.000001);
    }

    @Test
    public void agg_CountInt() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(2, s.agg(Exp.count()).get(0));
    }

    @Test
    public void max() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(56, s.max());
    }

    @Test
    public void agg_MaxInt() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(56, s.agg(Exp.$int("").max()).get(0));
    }

    @Test
    public void max_AllNegative() {
        IntArraySeries s = new IntArraySeries(-3, -2, -30, -56, -8);
        assertEquals(-2, s.max());
    }

    @Test
    public void aggMaxLong() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(56L, s.agg(Exp.$long("").max()).get(0));
    }

    @Test
    public void agg_MaxDouble() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(56., s.agg(Exp.$double("").max()).get(0));
    }

    @Test
    public void min() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2, s.min());
    }

    @Test
    public void min_AllPositive() {
        IntArraySeries s = new IntArraySeries(3, 2, 30, 56, 8);
        assertEquals(2, s.min());
    }

    @Test
    public void min_AllNegative() {
        IntArraySeries s = new IntArraySeries(-3, -2, -30, -56, -8);
        assertEquals(-56, s.min());
    }

    @Test
    public void agg_MinInt() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2, s.agg(Exp.$int("").min()).get(0));
    }

    @Test
    public void agg_MinLong() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2L, s.agg(Exp.$long("").min()).get(0));
    }

    @Test
    public void agg_MinDouble() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2., s.agg(Exp.$double("").min()).get(0));
    }

    @Test
    public void average() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(13.2, s.avg(), 0.000001);
    }

    @Test
    public void median() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(3, s.median(), 0.000001);
    }
}
