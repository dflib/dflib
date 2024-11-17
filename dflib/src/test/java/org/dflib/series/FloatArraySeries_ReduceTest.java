package org.dflib.series;

import org.dflib.Exp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FloatArraySeries_ReduceTest {

    @Test
    public void sum() {
        FloatArraySeries s = new FloatArraySeries(1.1f, 2f);
        assertEquals(3.1, s.sum(), 0.0000001f);
    }

    @Test
    public void reduce_SumInt() {
        FloatArraySeries s = new FloatArraySeries(1f, 2.55f);
        assertEquals(3, s.reduce(Exp.$int("").sum()));
    }

    @Test
    public void reduce_SumLong() {
        FloatArraySeries s = new FloatArraySeries(1.1f, 2f);
        assertEquals(3L, s.reduce(Exp.$long("").sum()));
    }

    @Test
    public void reduce_SumDouble() {
        FloatArraySeries s = new FloatArraySeries(1.1f, 2f);
        assertEquals(3.1, (Double) s.reduce(Exp.$double("").sum()), 0.00001f);
    }

    @Test
    public void reduce_CountInt() {
        FloatArraySeries s = new FloatArraySeries(1.1f, 2.2f);
        assertEquals(Integer.valueOf(2), s.reduce(Exp.count()));
    }

    @Test
    public void max() {
        FloatArraySeries s = new FloatArraySeries(1, -2, 3, 56.1f, 8);
        assertEquals(56.1f, s.max(), 0.0000001f);
    }

    @Test
    public void reduce_MaxInt() {
        FloatArraySeries s = new FloatArraySeries(1, -2, 3, 56.1f, 8);
        assertEquals(56, s.reduce(Exp.$int("").max()));
    }

    @Test
    public void reduce_MaxLong() {
        FloatArraySeries s = new FloatArraySeries(1, -2, 3, 56.1f, 8);
        assertEquals(56L, s.reduce(Exp.$long("").max()));
    }

    @Test
    public void reduce_MaxDouble() {
        FloatArraySeries s = new FloatArraySeries(1, -2, 3, 56.1f, 8);
        assertEquals(56.1, (Double) s.reduce(Exp.$double("").max()), 0.00001);
    }

    @Test
    public void min() {
        FloatArraySeries s = new FloatArraySeries(1, -2.9f, 3, 56, 8);
        assertEquals(-2.9f, s.min(), 0.00000001f);
    }

    @Test
    public void reduce_MinInt() {
        FloatArraySeries s = new FloatArraySeries(1, -2.9f, 3, 56, 8);
        assertEquals(-2, s.reduce(Exp.$int("").min()));
    }

    @Test
    public void reduce_MinLong() {
        FloatArraySeries s = new FloatArraySeries(1, -2.9f, 3, 56, 8);
        assertEquals(-2L, s.reduce(Exp.$long("").min()));
    }

    @Test
    public void reduce_MinDouble() {
        FloatArraySeries s = new FloatArraySeries(1, -2.9f, 3, 56, 8);
        assertEquals(-2.9, (Double) s.reduce(Exp.$double("").min()), 0.000001);
    }

    @Test
    public void average() {
        FloatArraySeries s = new FloatArraySeries(1.5f, -2.1f, 3.7f, 56.6f, 8.8f);
        assertEquals(13.7f, s.avg(), 0.0000001f);
    }

    @Test
    public void median() {
        FloatArraySeries s1 = new FloatArraySeries(1.5f, -2.1f, 3.7f, 56.6f, 8.8f);
        assertEquals(3.7f, s1.median(), 0.0000001);

        FloatArraySeries s2 = new FloatArraySeries(100.f, 55.5f, 0f, 5f);
        assertEquals(30.25f, s2.median(), 0.0000001);
    }

    @Test
    public void quantile025() {
        FloatArraySeries s1 = new FloatArraySeries(1.5f, -2.1f, 3.7f, 56.6f, 8.8f);
        assertEquals(1.5f, s1.quantile(0.25), 0.0000001);

        FloatArraySeries s2 = new FloatArraySeries(100.f, 55.5f, 0f, 5f);
        assertEquals(3.75f, s2.quantile(0.25), 0.0000001);
    }

    @Test
    public void quantile075() {
        FloatArraySeries s1 = new FloatArraySeries(1.5f, -2.1f, 3.7f, 56.6f, 8.8f);
        assertEquals(8.8f, s1.quantile(0.75), 0.0000001);

        FloatArraySeries s2 = new FloatArraySeries(100.f, 55.5f, 0f, 5f);
        assertEquals(66.625f, s2.quantile(0.75), 0.0000001);
    }

    @Test
    public void quantile090() {
        FloatArraySeries s = new FloatArraySeries(100.f, 55.5f, 0f, 5f);
        assertEquals(86.65f, s.quantile(0.90), 0.0000001);
    }
}
