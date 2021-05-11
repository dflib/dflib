package com.nhl.dflib.series;

import com.nhl.dflib.Exp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoubleArraySeries_AggTest {

    @Test
    public void testSum() {
        DoubleArraySeries s = new DoubleArraySeries(1.1, 2);
        assertEquals(3.1, s.sum(), 0.0000001);
    }

    @Test
    public void testAgg_SumInt() {
        DoubleArraySeries s = new DoubleArraySeries(1, 2.55);
        assertEquals(3, s.agg(Exp.$int("").sum()).get(0));
    }

    @Test
    public void testAgg_SumLong() {
        DoubleArraySeries s = new DoubleArraySeries(1.1, 2);
        assertEquals(3L, s.agg(Exp.$long("").sum()).get(0));
    }

    @Test
    public void testAgg_SumDouble() {
        DoubleArraySeries s = new DoubleArraySeries(1.1, 2);
        assertEquals(3.1, (Double) s.agg(Exp.$double("").sum()).get(0), 0.00001);
    }

    @Test
    public void testAgg_CountInt() {
        DoubleArraySeries s = new DoubleArraySeries(1.1, 2.2);
        assertEquals(Integer.valueOf(2), s.agg(Exp.count()).get(0));
    }

    @Test
    public void testMax() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2, 3, 56.1, 8);
        assertEquals(56.1, s.max(), 0.0000001);
    }

    @Test
    public void testAgg_MaxInt() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2, 3, 56.1, 8);
        assertEquals(56, s.agg(Exp.$int("").max()).get(0));
    }

    @Test
    public void testAgg_MaxLong() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2, 3, 56.1, 8);
        assertEquals(56L, s.agg(Exp.$long("").max()).get(0));
    }

    @Test
    public void testAgg_MaxDouble() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2, 3, 56.1, 8);
        assertEquals(56.1, (Double) s.agg(Exp.$double("").max()).get(0), 0.000001);
    }

    @Test
    public void testMin() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2.9, 3, 56, 8);
        assertEquals(-2.9, s.min(), 0.00000001);
    }

    @Test
    public void testAgg_MinInt() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2.9, 3, 56, 8);
        assertEquals(-2, s.agg(Exp.$int("").min()).get(0));
    }

    @Test
    public void testAgg_MinLong() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2.9, 3, 56, 8);
        assertEquals(-2L, s.agg(Exp.$long("").min()).get(0));
    }

    @Test
    public void testAgg_MinDouble() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2.9, 3, 56, 8);
        assertEquals(-2.9, (Double) s.agg(Exp.$double("").min()).get(0), 0.000001);
    }

    @Test
    public void testAverage() {
        DoubleArraySeries s = new DoubleArraySeries(1.5, -2.1, 3.7, 56.6, 8.8);
        assertEquals(13.7, s.average(), 0.0000001);
    }

    @Test
    public void testMedian() {
        DoubleArraySeries s = new DoubleArraySeries(1.5, -2.1, 3.7, 56.6, 8.8);
        assertEquals(3.7, s.median(), 0.0000001);
    }
}
