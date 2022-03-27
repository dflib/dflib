package com.nhl.dflib.series;

import com.nhl.dflib.Exp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SingleDoubleValueSeries_AggTest {
    @Test
    public void testSum() {
        DoubleSingleValueSeries s = new DoubleSingleValueSeries(1.1, 2);
        assertEquals(2.2, s.sum(), 0.0000001);
    }

    @Test
    public void testAgg_SumInt() {
        DoubleSingleValueSeries s = new DoubleSingleValueSeries(2., 2);
        assertEquals(4, s.agg(Exp.$int("").sum()).get(0));
    }

    @Test
    public void testAgg_SumLong() {
        DoubleSingleValueSeries s = new DoubleSingleValueSeries(1.1, 2);
        assertEquals(2L, s.agg(Exp.$long("").sum()).get(0));
    }

    @Test
    public void testAgg_SumDouble() {
        DoubleSingleValueSeries s = new DoubleSingleValueSeries(1.1, 2);
        assertEquals(2.2, (Double) s.agg(Exp.$double("").sum()).get(0), 0.00001);
    }

    @Test
    public void testAgg_CountInt() {
        DoubleSingleValueSeries s = new DoubleSingleValueSeries(1.1, 2);
        assertEquals(Integer.valueOf(2), s.agg(Exp.count()).get(0));
    }

    @Test
    public void testMax() {
        DoubleSingleValueSeries s = new DoubleSingleValueSeries(1.1, 2);

        assertEquals(1.1, s.max(), 0.0000001);
    }

    @Test
    public void testAgg_MaxInt() {
        DoubleSingleValueSeries s = new DoubleSingleValueSeries(1.1, 2);
        assertEquals(1, s.agg(Exp.$int("").max()).get(0));
    }

    @Test
    public void testAgg_MaxLong() {
        DoubleSingleValueSeries s = new DoubleSingleValueSeries(1.1, 2);
        assertEquals(1L, s.agg(Exp.$long("").max()).get(0));
    }

    @Test
    public void testAgg_MaxDouble() {
        DoubleSingleValueSeries s = new DoubleSingleValueSeries(1.1, 2);
        assertEquals(1.1, (Double) s.agg(Exp.$double("").max()).get(0), 0.000001);
    }

    @Test
    public void testMin() {
        DoubleSingleValueSeries s = new DoubleSingleValueSeries(1.1, 2);
        assertEquals(1.1, s.min(), 0.00000001);
    }

    @Test
    public void testAgg_MinInt() {
        DoubleSingleValueSeries s = new DoubleSingleValueSeries(1.1, 2);
        assertEquals(1, s.agg(Exp.$int("").min()).get(0));
    }

    @Test
    public void testAgg_MinLong() {
        DoubleSingleValueSeries s = new DoubleSingleValueSeries(1.1, 2);
        assertEquals(1L, s.agg(Exp.$long("").min()).get(0));
    }

    @Test
    public void testAgg_MinDouble() {
        DoubleSingleValueSeries s = new DoubleSingleValueSeries(1.1, 2);
        assertEquals(1.1, (Double) s.agg(Exp.$double("").min()).get(0), 0.000001);
    }

    @Test
    public void testAverage() {
        DoubleSingleValueSeries s = new DoubleSingleValueSeries(1.1, 2);
        assertEquals(1.1, s.avg(), 0.0000001);
    }

    @Test
    public void testMedian() {
        DoubleSingleValueSeries s = new DoubleSingleValueSeries(1.1, 2);
        assertEquals(1.1, s.median(), 0.0000001);
    }
}
