package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BooleanSeries_AggTest {

    @Test
    public void countTrue() {
        BooleanSeries s = Series.ofBool(true, false, true, false, true);
        assertEquals(3, s.countTrue());
    }

    @Test
    public void countFalse() {
        BooleanSeries s = Series.ofBool(true, true, false, true, false);
        assertEquals(2, s.countFalse());
    }

    @Test
    public void cumSum() {
        BooleanSeries s = Series.ofBool(true, false, true, true);
        new SeriesAsserts(s.cumSum()).expectData(1, 1, 2, 3);
    }
}
