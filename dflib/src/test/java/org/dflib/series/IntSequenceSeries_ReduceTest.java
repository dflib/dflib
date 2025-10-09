package org.dflib.series;

import org.dflib.Exp;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntSequenceSeries_ReduceTest {

    @Test
    public void sum() {
        IntSequenceSeries s = new IntSequenceSeries(1, 3);
        assertEquals(3, s.sum());
    }

    @Test
    public void reduce_SumInt() {
        IntSequenceSeries s = new IntSequenceSeries(1, 3);
        assertEquals(3L, s.reduce(Exp.$int("").sum()));
    }

    @Test
    public void reduce_SumLong() {
        IntSequenceSeries s = new IntSequenceSeries(1, 3);
        assertEquals(3L, s.reduce(Exp.$long("").sum()));
    }

    @Test
    public void reduce_SumDouble() {
        IntSequenceSeries s = new IntSequenceSeries(1, 3);
        assertEquals(3., s.reduce(Exp.$double("").sum()));
    }

    @Test
    public void reduce_Count() {
        IntSequenceSeries s = new IntSequenceSeries(1, 3);
        assertEquals(2, s.reduce(Exp.count()));
    }

    @Test
    public void max() {
        IntSequenceSeries s = new IntSequenceSeries(1, 15);
        assertEquals(14, s.max());
    }

    @Test
    public void reduce_MaxInt() {
        IntSequenceSeries s = new IntSequenceSeries(1, 15);
        assertEquals(14, s.reduce(Exp.$int("").max()));
    }

    @Test
    public void reduce_MaxLong() {
        IntSequenceSeries s = new IntSequenceSeries(1, 15);
        assertEquals(14L, s.reduce(Exp.$long("").max()));
    }

    @Test
    public void reduce_MaxDouble() {
        IntSequenceSeries s = new IntSequenceSeries(1, 15);
        assertEquals(14., s.reduce(Exp.$double("").max()));
    }

    @Test
    public void min() {
        IntSequenceSeries s = new IntSequenceSeries(-1, 15);
        assertEquals(-1, s.min());
    }

    @Test
    public void reduce_MinInt() {
        IntSequenceSeries s = new IntSequenceSeries(-1, 15);
        assertEquals(-1, s.reduce(Exp.$int("").min()));
    }

    @Test
    public void reduce_MinLong() {
        IntSequenceSeries s = new IntSequenceSeries(-1, 15);
        assertEquals(-1L, s.reduce(Exp.$long("").min()));
    }

    @Test
    public void reduce_MinDouble() {
        IntSequenceSeries s = new IntSequenceSeries(-1, 15);
        assertEquals(-1., s.reduce(Exp.$double("").min()));
    }

    @Test
    public void average() {
        IntSequenceSeries s1 = new IntSequenceSeries(-1, 5);
        assertEquals(1.5, s1.avg(), 0.000001);

        IntSequenceSeries s2 = new IntSequenceSeries(1, 5);
        assertEquals(2.5, s2.avg(), 0.000001);
    }

    @Test
    public void cumSum() {
        IntSequenceSeries s = new IntSequenceSeries(6, 9);
        new SeriesAsserts(s.cumSum()).expectData(6L, 13L, 21L);
    }

    @Test
    public void median() {

        IntSequenceSeries se = new IntSequenceSeries(5, 5);
        assertEquals(0., se.median(), 0.000001);

        IntSequenceSeries s0 = new IntSequenceSeries(5, 6);
        assertEquals(5., s0.median(), 0.000001);

        IntSequenceSeries s1 = new IntSequenceSeries(-1, 5);
        assertEquals(1.5, s1.median(), 0.000001);

        IntSequenceSeries s2 = new IntSequenceSeries(-1, 4);
        assertEquals(1, s2.median(), 0.000001);
    }
}
