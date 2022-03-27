package com.nhl.dflib.series;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SingleDoubleValueSeriesTest  {
    @Test
    public void testGetDouble() {
        DoubleSingleValueSeries s = new DoubleSingleValueSeries(1., 2);
        assertEquals(1, s.getDouble(0));
        assertEquals(1, s.getDouble(1));
    }


    @Test
    public void testGetInt_Offset_OutOfBounds() {
        DoubleSingleValueSeries s = new DoubleSingleValueSeries(1., 2);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> s.getDouble(3));
    }
}
