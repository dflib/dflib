package com.nhl.dflib.series;

import com.nhl.dflib.Exp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IntSingleValueSeries_AggTest {

    @Test
    public void testFirst() {
        IntSingleValueSeries s1 = new IntSingleValueSeries(1, 2);
        assertEquals(Integer.valueOf(1), s1.first());
    }

    @Test
    public void testSum() {
        IntSingleValueSeries s = new IntSingleValueSeries(1, 2);
        assertEquals(2, s.sum());
    }

    @Test
    public void testAgg_SumInt() {
        IntSingleValueSeries s = new IntSingleValueSeries(1, 2);
        assertEquals(2, s.agg(Exp.$int("").sum()).get(0));
    }

    @Test
    public void testAgg_SumLong() {
        IntSingleValueSeries s = new IntSingleValueSeries(1, 2);
        assertEquals(2L, s.agg(Exp.$long("").sum()).get(0));
    }

    @Test
    public void testAgg_SumDouble() {
        IntSingleValueSeries s = new IntSingleValueSeries(1, 2);
        assertEquals(2., (Double) s.agg(Exp.$double("").sum()).get(0), 0.000001);
    }

    @Test
    public void testAgg_CountInt() {
        IntSingleValueSeries s = new IntSingleValueSeries(1, 2);
        assertEquals(2, s.agg(Exp.count()).get(0));
    }

    @Test
    public void testMax() {
        IntSingleValueSeries s = new IntSingleValueSeries(1, 2);
        assertEquals(1, s.max());
    }

    @Test
    public void testAgg_MaxInt() {
        IntSingleValueSeries s = new IntSingleValueSeries(1, 2);
        assertEquals(1, s.agg(Exp.$int("").max()).get(0));
    }

    @Test
    public void testAggMaxLong() {
        IntSingleValueSeries s = new IntSingleValueSeries(1, 2);
        assertEquals(1L, s.agg(Exp.$long("").max()).get(0));
    }

    @Test
    public void testAgg_MaxDouble() {
        IntSingleValueSeries s = new IntSingleValueSeries(1, 2);
        assertEquals(1., s.agg(Exp.$double("").max()).get(0));
    }

    @Test
    public void testMin() {
        IntSingleValueSeries s = new IntSingleValueSeries(1, 2);
        assertEquals(1, s.min());
    }

    @Test
    public void testAgg_MinInt() {
        IntSingleValueSeries s = new IntSingleValueSeries(1, 2);
        assertEquals(1, s.agg(Exp.$int("").min()).get(0));
    }

    @Test
    public void testAgg_MinLong() {
        IntSingleValueSeries s = new IntSingleValueSeries(1, 2);
        assertEquals(1L, s.agg(Exp.$long("").min()).get(0));
    }

    @Test
    public void testAgg_MinDouble() {
        IntSingleValueSeries s = new IntSingleValueSeries(1, 2);
        assertEquals(1., s.agg(Exp.$double("").min()).get(0));
    }

    @Test
    public void testAverage() {
        IntSingleValueSeries s = new IntSingleValueSeries(1, 2);
        assertEquals(1, s.avg(), 0.000001);
    }

    @Test
    public void testMedian() {
        IntSingleValueSeries s = new IntSingleValueSeries(1, 2);
        assertEquals(1, s.median(), 0.000001);
    }
}
