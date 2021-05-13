package com.nhl.dflib.series;

import com.nhl.dflib.Exp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IntSequenceSeries_AggTest {

    @Test
    public void testSum() {
        IntSequenceSeries s = new IntSequenceSeries(1, 3);
        assertEquals(3, s.sum());
    }

    @Test
    public void testAgg_SumInt() {
        IntSequenceSeries s = new IntSequenceSeries(1, 3);
        assertEquals(3, s.agg(Exp.$int("").sum()).get(0));
    }

    @Test
    public void testAgg_SumLong() {
        IntSequenceSeries s = new IntSequenceSeries(1, 3);
        assertEquals(3L, s.agg(Exp.$long("").sum()).get(0));
    }

    @Test
    public void testAgg_SumDouble() {
        IntSequenceSeries s = new IntSequenceSeries(1, 3);
        assertEquals(3., s.agg(Exp.$double("").sum()).get(0));
    }

    @Test
    public void testAgg_Count() {
        IntSequenceSeries s = new IntSequenceSeries(1, 3);
        assertEquals(2, s.agg(Exp.count()).get(0));
    }

    @Test
    public void testMax() {
        IntSequenceSeries s = new IntSequenceSeries(1, 15);
        assertEquals(14, s.max());
    }

    @Test
    public void testAgg_MaxInt() {
        IntSequenceSeries s = new IntSequenceSeries(1, 15);
        assertEquals(14, s.agg(Exp.$int("").max()).get(0));
    }

    @Test
    public void testAggMaxLong() {
        IntSequenceSeries s = new IntSequenceSeries(1, 15);
        assertEquals(14L, s.agg(Exp.$long("").max()).get(0));
    }

    @Test
    public void testAgg_MaxDouble() {
        IntSequenceSeries s = new IntSequenceSeries(1, 15);
        assertEquals(14., s.agg(Exp.$double("").max()).get(0));
    }

    @Test
    public void testMin() {
        IntSequenceSeries s = new IntSequenceSeries(-1, 15);
        assertEquals(-1, s.min());
    }

    @Test
    public void testAgg_MinInt() {
        IntSequenceSeries s = new IntSequenceSeries(-1, 15);
        assertEquals(-1, s.agg(Exp.$int("").min()).get(0));
    }

    @Test
    public void testAgg_MinLong() {
        IntSequenceSeries s = new IntSequenceSeries(-1, 15);
        assertEquals(-1L, s.agg(Exp.$long("").min()).get(0));
    }

    @Test
    public void testAgg_MinDouble() {
        IntSequenceSeries s = new IntSequenceSeries(-1, 15);
        assertEquals(-1., s.agg(Exp.$double("").min()).get(0));
    }

    @Test
    public void testAverage() {
        IntSequenceSeries s1 = new IntSequenceSeries(-1, 5);
        assertEquals(1.5, s1.avg(), 0.000001);

        IntSequenceSeries s2 = new IntSequenceSeries(1, 5);
        assertEquals(2.5, s2.avg(), 0.000001);
    }

    @Test
    public void testMedian() {
        IntSequenceSeries s1 = new IntSequenceSeries(-1, 5);
        assertEquals(1.5, s1.median(), 0.000001);

        IntSequenceSeries s2 = new IntSequenceSeries(-1, 4);
        assertEquals(1, s2.median(), 0.000001);
    }
}
