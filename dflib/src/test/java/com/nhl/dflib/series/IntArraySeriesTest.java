package com.nhl.dflib.series;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntArraySeriesTest {

    @Test
    public void testGetInt() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(1, s.getInt(0));
        assertEquals(2, s.getInt(1));
    }

    @Test
    public void testGetInt_Offset() {
        IntArraySeries s = new IntArraySeries(new int[]{1, 2, 3, 4}, 1, 2);
        assertEquals(2, s.getInt(0));
        assertEquals(3, s.getInt(1));
    }

    @Test
    public void testGetInt_Offset_OutOfBounds() {
        IntArraySeries s = new IntArraySeries(new int[]{1, 2, 3, 4}, 1, 2);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> s.getInt(2));
    }
}
