package org.dflib.series;

import org.dflib.Exp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Deprecated
public class LongArraySeries_AggTest {

    @Test
    public void agg_SumInt() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(3, s.agg(Exp.$int("").sum()).get(0));
    }

    @Test
    public void agg_SumLong() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(3L, s.agg(Exp.$long("").sum()).get(0));
    }

    @Test
    public void agg_SumDouble() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(3., s.agg(Exp.$double("").sum()).get(0));
    }

    @Test
    public void agg_Count() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(2, s.agg(Exp.count()).get(0));
    }

    @Test
    public void agg_MaxInt() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(56, s.agg(Exp.$int("").max()).get(0));
    }

    @Test
    public void agg_MaxLong() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(56L, s.agg(Exp.$long("").max()).get(0));
    }

    @Test
    public void agg_MaxDouble() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(56., s.agg(Exp.$double("").max()).get(0));
    }

    @Test
    public void agg_MinInt() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2, s.agg(Exp.$int("").min()).get(0));
    }

    @Test
    public void agg_MinLong() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2L, s.agg(Exp.$long("").min()).get(0));
    }

    @Test
    public void agg_MinDouble() {
        LongArraySeries s = new LongArraySeries(1, -2, 3, 56, 8);
        assertEquals(-2., s.agg(Exp.$double("").min()).get(0));
    }
}
