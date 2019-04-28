package com.nhl.dflib.series;

import org.junit.Test;

import static org.junit.Assert.*;

public class BooleanArraySeriesTest {

    @Test
    public void testGetBoolean() {
        BooleanArraySeries s = new BooleanArraySeries(true, false);
        assertEquals(true, s.getBoolean(0));
        assertEquals(false, s.getBoolean(1));
    }

    @Test
    public void testGetBoolean_Offset() {
        BooleanArraySeries s = new BooleanArraySeries(new boolean[]{true, false, true, false}, 1, 2);
        assertEquals(false, s.getBoolean(0));
        assertEquals(true, s.getBoolean(1));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testGetBoolean_Offset_OutOfBounds() {
        BooleanArraySeries s = new BooleanArraySeries(new boolean[]{true, false, true, false}, 1, 2);
        s.getBoolean(2);
    }
}
