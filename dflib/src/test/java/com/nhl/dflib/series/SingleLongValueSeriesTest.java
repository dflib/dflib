package com.nhl.dflib.series;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SingleLongValueSeriesTest {

    @Test
    public void testGetLong() {
        SingleLongValueSeries s = new SingleLongValueSeries(1L, 2);
        assertEquals(1, s.getLong(0));
        assertEquals(1, s.getLong(1));
    }


    @Test
    public void testGetInt_Offset_OutOfBounds() {
        SingleLongValueSeries s = new SingleLongValueSeries(1L, 2);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> s.getLong(3));
    }
}
