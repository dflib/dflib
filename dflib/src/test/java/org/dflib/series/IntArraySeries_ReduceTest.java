package org.dflib.series;

import org.dflib.Exp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class IntArraySeries_ReduceTest {

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
    public void reduce_SumInt() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(3, s.reduce(Exp.$int("").sum()));
    }

    @Test
    public void reduce_SumLong() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(3L, s.reduce(Exp.$long("").sum()));
    }

    @Test
    public void reduce_SumDouble() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(3., (Double) s.reduce(Exp.$double("").sum()), 0.000001);
    }

    @Test
    public void reduce_CountInt() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(2, s.reduce(Exp.count()));
    }

    @Test
    public void max() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(56, s.max());
    }

    @Test
    public void reduce_MaxInt() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(56, s.reduce(Exp.$int("").max()));
    }

    @Test
    public void aggMaxLong() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(56L, s.reduce(Exp.$long("").max()));
    }

    @Test
    public void reduce_MaxDouble() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(56., s.reduce(Exp.$double("").max()));
    }

    @Test
    public void min() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2, s.min());
    }

    @Test
    public void reduce_MinInt() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2, s.reduce(Exp.$int("").min()));
    }

    @Test
    public void reduce_MinLong() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2L, s.reduce(Exp.$long("").min()));
    }

    @Test
    public void reduce_MinDouble() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2., s.reduce(Exp.$double("").min()));
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
