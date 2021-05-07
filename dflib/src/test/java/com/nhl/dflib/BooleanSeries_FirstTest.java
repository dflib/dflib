package com.nhl.dflib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BooleanSeries_FirstTest {

    @Test
    public void testFirstTrue() {
        assertEquals(-1, BooleanSeries.forBooleans().firstTrue());
        assertEquals(0, BooleanSeries.forBooleans(true, true, true).firstTrue());
        assertEquals(2, BooleanSeries.forBooleans(false, false, true).firstTrue());
        assertEquals(-1, BooleanSeries.forBooleans(false, false, false).firstTrue());
    }
}
