package com.nhl.dflib.series;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IntSingleValueSeriesTest {

    @Test
    public void testGetInt() {
        IntSingleValueSeries s = new IntSingleValueSeries(1, 2);
        assertEquals(1, s.getInt(0));
        assertEquals(1, s.getInt(1));
    }


    @Test
    public void testGetInt_Offset_OutOfBounds() {
        IntSingleValueSeries s = new IntSingleValueSeries(1, 2);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> s.getInt(3));
    }
}
