package org.dflib.series;

import org.dflib.Exp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Deprecated
public class IntSequenceSeries_AggTest {

    @Test
    public void agg_SumInt() {
        IntSequenceSeries s = new IntSequenceSeries(1, 3);
        assertEquals(3L, s.agg(Exp.$int("").sum()).get(0));
    }

    @Test
    public void agg_SumLong() {
        IntSequenceSeries s = new IntSequenceSeries(1, 3);
        assertEquals(3L, s.agg(Exp.$long("").sum()).get(0));
    }

    @Test
    public void agg_SumDouble() {
        IntSequenceSeries s = new IntSequenceSeries(1, 3);
        assertEquals(3., s.agg(Exp.$double("").sum()).get(0));
    }

    @Test
    public void agg_Count() {
        IntSequenceSeries s = new IntSequenceSeries(1, 3);
        assertEquals(2, s.agg(Exp.count()).get(0));
    }

    @Test
    public void agg_MaxInt() {
        IntSequenceSeries s = new IntSequenceSeries(1, 15);
        assertEquals(14, s.agg(Exp.$int("").max()).get(0));
    }

    @Test
    public void aggMaxLong() {
        IntSequenceSeries s = new IntSequenceSeries(1, 15);
        assertEquals(14L, s.agg(Exp.$long("").max()).get(0));
    }

    @Test
    public void agg_MaxDouble() {
        IntSequenceSeries s = new IntSequenceSeries(1, 15);
        assertEquals(14., s.agg(Exp.$double("").max()).get(0));
    }

    @Test
    public void agg_MinInt() {
        IntSequenceSeries s = new IntSequenceSeries(-1, 15);
        assertEquals(-1, s.agg(Exp.$int("").min()).get(0));
    }

    @Test
    public void agg_MinLong() {
        IntSequenceSeries s = new IntSequenceSeries(-1, 15);
        assertEquals(-1L, s.agg(Exp.$long("").min()).get(0));
    }

    @Test
    public void agg_MinDouble() {
        IntSequenceSeries s = new IntSequenceSeries(-1, 15);
        assertEquals(-1., s.agg(Exp.$double("").min()).get(0));
    }
}
