package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

import static org.junit.Assert.*;

public class BooleanSeries_SelectTest {

    @Test
    public void testPositional() {
        Series<Boolean> s = BooleanSeries.forBooleans(true, false, true).select(2, 1);
        new SeriesAsserts(s).expectData(true, false);
        assertTrue(s instanceof BooleanSeries);
    }

    @Test
    public void testPositional_Empty() {
        Series<Boolean> s = BooleanSeries.forBooleans(true, false, true).select();
        new SeriesAsserts(s).expectData();
        assertTrue(s instanceof BooleanSeries);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testPosition_OutOfBounds() {
        BooleanSeries.forBooleans(true, false, true).select(0, 3);
    }

    @Test
    public void testPositional_Nulls() {
        Series<Boolean> s = BooleanSeries.forBooleans(true, false, true).select(2, 1, -1);
        new SeriesAsserts(s).expectData(true, false, null);
        assertFalse(s instanceof BooleanSeries);
    }

    @Test
    public void testBoolean() {
        BooleanSeries condition = BooleanSeries.forBooleans(false, true, true);
        Series<Boolean> s = BooleanSeries.forBooleans(true, false, true).filter(condition);
        new SeriesAsserts(s).expectData(false, true);
        assertTrue(s instanceof BooleanSeries);
    }
}
