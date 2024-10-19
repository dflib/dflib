package org.dflib.series;

import org.dflib.Exp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Deprecated
public class IntArraySeries_AggTest {

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
    public void agg_MaxInt() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(56, s.agg(Exp.$int("").max()).get(0));
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
}
