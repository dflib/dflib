package org.dflib.series;

import org.dflib.Exp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FloatArraySeries_AggTest {

    @Test
    public void sum() {
        FloatArraySeries s = new FloatArraySeries(1.1f, 2f);
        assertEquals(3.1, s.sum(), 0.0000001f);
    }

    @Test
    public void agg_SumInt() {
        FloatArraySeries s = new FloatArraySeries(1f, 2.55f);
        assertEquals(3, s.agg(Exp.$int("").sum()).get(0));
    }

    @Test
    public void agg_SumLong() {
        FloatArraySeries s = new FloatArraySeries(1.1f, 2f);
        assertEquals(3L, s.agg(Exp.$long("").sum()).get(0));
    }

    @Test
    public void agg_SumDouble() {
        FloatArraySeries s = new FloatArraySeries(1.1f, 2f);
        assertEquals(3.1, (Double) s.agg(Exp.$double("").sum()).get(0), 0.00001f);
    }

    @Test
    public void agg_CountInt() {
        FloatArraySeries s = new FloatArraySeries(1.1f, 2.2f);
        assertEquals(Integer.valueOf(2), s.agg(Exp.count()).get(0));
    }

    @Test
    public void max() {
        FloatArraySeries s = new FloatArraySeries(1, -2, 3, 56.1f, 8);
        assertEquals(56.1f, s.max(), 0.0000001f);
    }

    @Test
    public void agg_MaxInt() {
        FloatArraySeries s = new FloatArraySeries(1, -2, 3, 56.1f, 8);
        assertEquals(56, s.agg(Exp.$int("").max()).get(0));
    }

    @Test
    public void agg_MaxLong() {
        FloatArraySeries s = new FloatArraySeries(1, -2, 3, 56.1f, 8);
        assertEquals(56L, s.agg(Exp.$long("").max()).get(0));
    }

    @Test
    public void agg_MaxDouble() {
        FloatArraySeries s = new FloatArraySeries(1, -2, 3, 56.1f, 8);
        assertEquals(56.1, (Double) s.agg(Exp.$double("").max()).get(0), 0.00001);
    }

    @Test
    public void min() {
        FloatArraySeries s = new FloatArraySeries(1, -2.9f, 3, 56, 8);
        assertEquals(-2.9f, s.min(), 0.00000001f);
    }

    @Test
    public void agg_MinInt() {
        FloatArraySeries s = new FloatArraySeries(1, -2.9f, 3, 56, 8);
        assertEquals(-2, s.agg(Exp.$int("").min()).get(0));
    }

    @Test
    public void agg_MinLong() {
        FloatArraySeries s = new FloatArraySeries(1, -2.9f, 3, 56, 8);
        assertEquals(-2L, s.agg(Exp.$long("").min()).get(0));
    }

    @Test
    public void agg_MinDouble() {
        FloatArraySeries s = new FloatArraySeries(1, -2.9f, 3, 56, 8);
        assertEquals(-2.9, (Double) s.agg(Exp.$double("").min()).get(0), 0.000001);
    }

    @Test
    public void average() {
        FloatArraySeries s = new FloatArraySeries(1.5f, -2.1f, 3.7f, 56.6f, 8.8f);
        assertEquals(13.7f, s.avg(), 0.0000001f);
    }

    @Test
    public void median() {
        FloatArraySeries s = new FloatArraySeries(1.5f, -2.1f, 3.7f, 56.6f, 8.8f);
        assertEquals(3.7f, s.median(), 0.0000001f);
    }
}
