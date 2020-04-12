package com.nhl.dflib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
