package com.nhl.dflib;

import org.junit.Test;

import static org.junit.Assert.*;

public class BooleanSeries_TrueFalseTest {

    @Test
    public void testIsTrue() {
        assertTrue(BooleanSeries.forBooleans().isTrue());
        assertTrue(BooleanSeries.forBooleans(true, true, true).isTrue());
        assertFalse(BooleanSeries.forBooleans(true, false, true).isTrue());
    }

    @Test
    public void testIsFalse() {
        assertFalse(BooleanSeries.forBooleans().isFalse());
        assertTrue(BooleanSeries.forBooleans(false, false, false).isFalse());
        assertFalse(BooleanSeries.forBooleans(true, false, true).isFalse());
    }
}
