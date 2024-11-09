package org.dflib.series;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BooleanArraySeries_AggTest {

    @Test
    public void countTrue() {
        BooleanArraySeries s = new BooleanArraySeries(true, false, true, false, true);
        assertEquals(3, s.countTrue());
    }

    @Test
    public void countFalse() {
        BooleanArraySeries s = new BooleanArraySeries(true, true, false, true, false);
        assertEquals(2, s.countFalse());
    }

    @Test
    public void cumSum() {
        BooleanArraySeries s = new BooleanArraySeries(true, false, true, true);
        new SeriesAsserts(s.cumSum()).expectData(1, 1, 2, 3);
    }
}
