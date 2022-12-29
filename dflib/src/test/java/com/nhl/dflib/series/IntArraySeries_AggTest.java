package com.nhl.dflib.series;

import com.nhl.dflib.Exp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class IntArraySeries_AggTest {

    @Test
    public void testFirst() {
        IntArraySeries s1 = new IntArraySeries(1, 2);
        assertEquals(Integer.valueOf(1), s1.first());

        IntArraySeries s2 = new IntArraySeries();
        assertNull(s2.first());
    }

    @Test
    public void testSum() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(3, s.sum());
    }

    @Test
    public void testAgg_SumInt() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(3, s.agg(Exp.$int("").sum()).get(0));
    }

    @Test
    public void testAgg_SumLong() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(3L, s.agg(Exp.$long("").sum()).get(0));
    }

    @Test
    public void testAgg_SumDouble() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(3., (Double) s.agg(Exp.$double("").sum()).get(0), 0.000001);
    }

    @Test
    public void testAgg_CountInt() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(2, s.agg(Exp.count()).get(0));
    }

    @Test
    public void testMax() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(56, s.max());
    }

    @Test
    public void testAgg_MaxInt() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(56, s.agg(Exp.$int("").max()).get(0));
    }

    @Test
    public void testAggMaxLong() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(56L, s.agg(Exp.$long("").max()).get(0));
    }

    @Test
    public void testAgg_MaxDouble() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(56., s.agg(Exp.$double("").max()).get(0));
    }

    @Test
    public void testMin() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2, s.min());
    }

    @Test
    public void testAgg_MinInt() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2, s.agg(Exp.$int("").min()).get(0));
    }

    @Test
    public void testAgg_MinLong() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2L, s.agg(Exp.$long("").min()).get(0));
    }

    @Test
    public void testAgg_MinDouble() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2., s.agg(Exp.$double("").min()).get(0));
    }

    @Test
    public void testAverage() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(13.2, s.avg(), 0.000001);
    }

    @Test
    public void testMedian() {
        IntArraySeries s = new IntArraySeries(1, -2, 3, 56, 8);
        assertEquals(3, s.median(), 0.000001);
    }
}
