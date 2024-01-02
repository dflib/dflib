package org.dflib.series;

import org.dflib.series.BooleanArraySeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
}
