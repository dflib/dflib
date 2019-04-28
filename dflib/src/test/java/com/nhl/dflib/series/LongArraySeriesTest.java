package com.nhl.dflib.series;

import org.junit.Test;

import static org.junit.Assert.*;

public class LongArraySeriesTest {

    @Test
    public void testGetLong() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(1, s.getLong(0));
        assertEquals(2, s.getLong(1));
    }

    @Test
    public void testGetLong_Offset() {
        LongArraySeries s = new LongArraySeries(new long[]{1, 2, 3, 4}, 1, 2);
        assertEquals(2, s.getLong(0));
        assertEquals(3, s.getLong(1));
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testGetLong_Offset_OutOfBounds() {
        LongArraySeries s = new LongArraySeries(new long[]{1, 2, 3, 4}, 1, 2);
        s.getLong(2);
    }
}
