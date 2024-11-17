package org.dflib.series;

import org.dflib.Exp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DoubleArraySeries_ReduceTest {

    @Test
    public void sum() {
        DoubleArraySeries s = new DoubleArraySeries(1.1, 2);
        assertEquals(3.1, s.sum(), 0.0000001);
    }

    @Test
    public void agg_SumInt() {
        DoubleArraySeries s = new DoubleArraySeries(1, 2.55);
        assertEquals(3, s.reduce(Exp.$int("").sum()));
    }

    @Test
    public void agg_SumLong() {
        DoubleArraySeries s = new DoubleArraySeries(1.1, 2);
        assertEquals(3L, s.reduce(Exp.$long("").sum()));
    }

    @Test
    public void agg_SumDouble() {
        DoubleArraySeries s = new DoubleArraySeries(1.1, 2);
        assertEquals(3.1, (Double) s.reduce(Exp.$double("").sum()), 0.00001);
    }

    @Test
    public void agg_CountInt() {
        DoubleArraySeries s = new DoubleArraySeries(1.1, 2.2);
        assertEquals(Integer.valueOf(2), s.reduce(Exp.count()));
    }

    @Test
    public void max() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2, 3, 56.1, 8);
        assertEquals(56.1, s.max(), 0.0000001);
    }

    @Test
    public void agg_MaxInt() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2, 3, 56.1, 8);
        assertEquals(56, s.reduce(Exp.$int("").max()));
    }

    @Test
    public void agg_MaxLong() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2, 3, 56.1, 8);
        assertEquals(56L, s.reduce(Exp.$long("").max()));
    }

    @Test
    public void agg_MaxDouble() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2, 3, 56.1, 8);
        assertEquals(56.1, (Double) s.reduce(Exp.$double("").max()), 0.000001);
    }

    @Test
    public void min() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2.9, 3, 56, 8);
        assertEquals(-2.9, s.min(), 0.00000001);
    }

    @Test
    public void agg_MinInt() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2.9, 3, 56, 8);
        assertEquals(-2, s.reduce(Exp.$int("").min()));
    }

    @Test
    public void agg_MinLong() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2.9, 3, 56, 8);
        assertEquals(-2L, s.reduce(Exp.$long("").min()));
    }

    @Test
    public void agg_MinDouble() {
        DoubleArraySeries s = new DoubleArraySeries(1, -2.9, 3, 56, 8);
        assertEquals(-2.9, (Double) s.reduce(Exp.$double("").min()), 0.000001);
    }

    @Test
    public void average() {
        DoubleArraySeries s = new DoubleArraySeries(1.5, -2.1, 3.7, 56.6, 8.8);
        assertEquals(13.7, s.avg(), 0.0000001);
    }

    @Test
    public void median() {
        DoubleArraySeries s1 = new DoubleArraySeries(1.5, -2.1, 3.7, 56.6, 8.8);
        assertEquals(3.7, s1.median(), 0.0000001);

        DoubleArraySeries s2 = new DoubleArraySeries(100., 55.5, 0., 5.0);
        assertEquals(30.25, s2.median(), 0.0000001);
    }

    @Test
    public void quantile025() {
        DoubleArraySeries s1 = new DoubleArraySeries(1.5, -2.1, 3.7, 56.6, 8.8);
        assertEquals(1.5, s1.quantile(0.25), 0.0000001);

        DoubleArraySeries s2 = new DoubleArraySeries(100., 55.5, 0., 5.0);
        assertEquals(3.75, s2.quantile(0.25), 0.0000001);
    }

    @Test
    public void quantile075() {
        DoubleArraySeries s1 = new DoubleArraySeries(1.5, -2.1, 3.7, 56.6, 8.8);
        assertEquals(8.8, s1.quantile(0.75), 0.0000001);

        DoubleArraySeries s2 = new DoubleArraySeries(100., 55.5, 0., 5.0);
        assertEquals(66.625, s2.quantile(0.75), 0.0000001);
    }

    @Test
    public void quantile090() {
        DoubleArraySeries s = new DoubleArraySeries(100., 55.5, 0., 5.0);
        assertEquals(86.65, s.quantile(0.90), 0.0000001);
    }
}
