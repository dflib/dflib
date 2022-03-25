package com.nhl.dflib.series;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LongSingleValueSeriesTest {

    @Test
    public void testGetLong() {
        LongSingleValueSeries s = new LongSingleValueSeries(1L, 2);
        assertEquals(1, s.getLong(0));
        assertEquals(1, s.getLong(1));
    }


    @Test
    public void testGetInt_Offset_OutOfBounds() {
        LongSingleValueSeries s = new LongSingleValueSeries(1L, 2);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> s.getLong(3));
    }
}
