package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

import static org.junit.Assert.*;

public class BooleanSeries_SelectTest {

    @Test
    public void test() {
        Series<Boolean> s = BooleanSeries.forBooleans(true, false, true).select(2, 1);
        new SeriesAsserts(s).expectData(true, false);
        assertTrue(s instanceof BooleanSeries);
    }

    @Test
    public void test_Empty() {
        Series<Boolean> s = BooleanSeries.forBooleans(true, false, true).select();
        new SeriesAsserts(s).expectData();
        assertTrue(s instanceof BooleanSeries);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void test_OutOfBounds() {
        BooleanSeries.forBooleans(true, false, true).select(0, 3);
    }

    @Test
    public void testNulls() {
        Series<Boolean> s = BooleanSeries.forBooleans(true, false, true).select(2, 1, -1);
        new SeriesAsserts(s).expectData(true, false, null);
        assertFalse(s instanceof BooleanSeries);
    }
}
