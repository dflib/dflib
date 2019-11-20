package com.nhl.dflib.series;

import org.junit.Test;

import static org.junit.Assert.*;

public class BooleanArraySeries_AggTest {

    @Test
    public void testCountTrue() {
        BooleanArraySeries s = new BooleanArraySeries(true, false, true, false, true);
        assertEquals(3, s.countTrue());
    }

    @Test
    public void testCountFalse() {
        BooleanArraySeries s = new BooleanArraySeries(true, true, false, true, false);
        assertEquals(2, s.countFalse());
    }
}
