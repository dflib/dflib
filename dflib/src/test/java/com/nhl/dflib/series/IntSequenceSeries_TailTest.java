package com.nhl.dflib.series;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSequenceSeries_TailTest {

    @Test
    public void tail2() {
        IntSeries s = new IntSequenceSeries(1, 4).tail(2);
        new IntSeriesAsserts(s).expectData(2, 3);
    }

    @Test
    public void tail1() {
        IntSeries s = new IntSequenceSeries(1, 4).tail(1);
        new IntSeriesAsserts(s).expectData(3);
    }

    @Test
    public void zero() {
        IntSeries s = new IntSequenceSeries(1, 3).tail(0);
        new IntSeriesAsserts(s).expectData();
    }

    @Test
    public void outOfBounds() {
        IntSeries s = new IntSequenceSeries(1, 4).tail(4);
        new IntSeriesAsserts(s).expectData(1, 2, 3);
    }

    @Test
    public void negative() {
        IntSeries s = new IntSequenceSeries(1, 4).tail(-2);
        new IntSeriesAsserts(s).expectData(1);
    }
}
