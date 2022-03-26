package com.nhl.dflib.series;

import com.nhl.dflib.Exp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SingleLongValueSeries_AggTest {
    @Test
    public void testSum() {
        SingleLongValueSeries s = new SingleLongValueSeries(1L, 2);
        assertEquals(2L, s.sum());
    }

    @Test
    public void testAgg_SumInt() {
        SingleLongValueSeries s = new SingleLongValueSeries(1L, 2);
        assertEquals(2, s.agg(Exp.$int("").sum()).get(0));
    }

    @Test
    public void testAgg_SumLong() {
        SingleLongValueSeries s = new SingleLongValueSeries(1L, 2);
        assertEquals(2L, s.agg(Exp.$long("").sum()).get(0));
    }

    @Test
    public void testAgg_SumDouble() {
        SingleLongValueSeries s = new SingleLongValueSeries(1L, 2);
        assertEquals(2., s.agg(Exp.$double("").sum()).get(0));
    }

    @Test
    public void testAgg_Count() {
        SingleLongValueSeries s = new SingleLongValueSeries(1L, 2);
        assertEquals(2, s.agg(Exp.count()).get(0));
    }

    @Test
    public void testMax() {
        SingleLongValueSeries s = new SingleLongValueSeries(2L, 2);
        assertEquals(2L, s.max());
    }

    @Test
    public void testAgg_MaxInt() {
        SingleLongValueSeries s = new SingleLongValueSeries(2L, 2);
        assertEquals(2, s.agg(Exp.$int("").max()).get(0));
    }

    @Test
    public void testAgg_MaxLong() {
        SingleLongValueSeries s = new SingleLongValueSeries(2L, 2);
        assertEquals(2L, s.agg(Exp.$long("").max()).get(0));
    }

    @Test
    public void testAgg_MaxDouble() {
        SingleLongValueSeries s = new SingleLongValueSeries(2L, 2);
        assertEquals(2., s.agg(Exp.$double("").max()).get(0));
    }

    @Test
    public void testMin() {
        SingleLongValueSeries s = new SingleLongValueSeries(2L, 2);
        assertEquals(2L, s.min());
    }

    @Test
    public void testAgg_MinInt() {
        SingleLongValueSeries s = new SingleLongValueSeries(2L, 2);
        assertEquals(2, s.agg(Exp.$int("").min()).get(0));
    }

    @Test
    public void testAgg_MinLong() {
        SingleLongValueSeries s = new SingleLongValueSeries(2L, 2);
        assertEquals(2L, s.agg(Exp.$long("").min()).get(0));
    }

    @Test
    public void testAgg_MinDouble() {
        SingleLongValueSeries s = new SingleLongValueSeries(2L, 2);
        assertEquals(2., s.agg(Exp.$double("").min()).get(0));
    }

    @Test
    public void testAverage() {
        SingleLongValueSeries s = new SingleLongValueSeries(2L, 2);
        assertEquals(2L, s.avg(), 0.000001);
    }

    @Test
    public void testMedian() {
        SingleLongValueSeries s = new SingleLongValueSeries(2L, 2);
        assertEquals(2L, s.median(), 0.000001);
    }
}
