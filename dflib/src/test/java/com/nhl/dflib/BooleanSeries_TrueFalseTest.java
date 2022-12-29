package com.nhl.dflib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BooleanSeries_TrueFalseTest {

    @Test
    public void testIsTrue() {
        assertTrue(Series.ofBool().isTrue());
        assertTrue(Series.ofBool(true, true, true).isTrue());
        assertFalse(Series.ofBool(true, false, true).isTrue());
    }

    @Test
    public void testIsFalse() {
        assertFalse(Series.ofBool().isFalse());
        assertTrue(Series.ofBool(false, false, false).isFalse());
        assertFalse(Series.ofBool(true, false, true).isFalse());
    }
}
