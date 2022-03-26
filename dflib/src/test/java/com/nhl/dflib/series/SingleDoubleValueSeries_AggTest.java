package com.nhl.dflib.series;

import com.nhl.dflib.Exp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SingleDoubleValueSeries_AggTest {
    @Test
    public void testSum() {
        SingleDoubleValueSeries s = new SingleDoubleValueSeries(1.1, 2);
        assertEquals(2.2, s.sum(), 0.0000001);
    }

    @Test
    public void testAgg_SumInt() {
        SingleDoubleValueSeries s = new SingleDoubleValueSeries(2., 2);
        assertEquals(4, s.agg(Exp.$int("").sum()).get(0));
    }

    @Test
    public void testAgg_SumLong() {
        SingleDoubleValueSeries s = new SingleDoubleValueSeries(1.1, 2);
        assertEquals(2, s.agg(Exp.$long("").sum()).get(0));
    }

    @Test
    public void testAgg_SumDouble() {
        SingleDoubleValueSeries s = new SingleDoubleValueSeries(1.1, 2);
        assertEquals(2.2, (Double) s.agg(Exp.$double("").sum()).get(0), 0.00001);
    }

    @Test
    public void testAgg_CountInt() {
        SingleDoubleValueSeries s = new SingleDoubleValueSeries(1.1, 2);
        assertEquals(Integer.valueOf(2), s.agg(Exp.count()).get(0));
    }

    @Test
    public void testMax() {
        SingleDoubleValueSeries s = new SingleDoubleValueSeries(1.1, 2);

        assertEquals(1.1, s.max(), 0.0000001);
    }

    @Test
    public void testAgg_MaxInt() {
        SingleDoubleValueSeries s = new SingleDoubleValueSeries(1.1, 2);
        assertEquals(1, s.agg(Exp.$int("").max()).get(0));
    }

    @Test
    public void testAgg_MaxLong() {
        SingleDoubleValueSeries s = new SingleDoubleValueSeries(1.1, 2);
        assertEquals(1L, s.agg(Exp.$long("").max()).get(0));
    }

    @Test
    public void testAgg_MaxDouble() {
        SingleDoubleValueSeries s = new SingleDoubleValueSeries(1.1, 2);
        assertEquals(1.1, (Double) s.agg(Exp.$double("").max()).get(0), 0.000001);
    }

    @Test
    public void testMin() {
        SingleDoubleValueSeries s = new SingleDoubleValueSeries(1.1, 2);
        assertEquals(1.1, s.min(), 0.00000001);
    }

    @Test
    public void testAgg_MinInt() {
        SingleDoubleValueSeries s = new SingleDoubleValueSeries(1.1, 2);
        assertEquals(1, s.agg(Exp.$int("").min()).get(0));
    }

    @Test
    public void testAgg_MinLong() {
        SingleDoubleValueSeries s = new SingleDoubleValueSeries(1.1, 2);
        assertEquals(1L, s.agg(Exp.$long("").min()).get(0));
    }

    @Test
    public void testAgg_MinDouble() {
        SingleDoubleValueSeries s = new SingleDoubleValueSeries(1.1, 2);
        assertEquals(1.1, (Double) s.agg(Exp.$double("").min()).get(0), 0.000001);
    }

    @Test
    public void testAverage() {
        SingleDoubleValueSeries s = new SingleDoubleValueSeries(1.1, 2);
        assertEquals(1.1, s.avg(), 0.0000001);
    }

    @Test
    public void testMedian() {
        SingleDoubleValueSeries s = new SingleDoubleValueSeries(1.1, 2);
        assertEquals(1.1, s.median(), 0.0000001);
    }
}
