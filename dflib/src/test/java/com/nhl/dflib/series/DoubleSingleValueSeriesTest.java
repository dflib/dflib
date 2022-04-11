package com.nhl.dflib.series;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DoubleSingleValueSeriesTest {
    @Test
    public void testGetDouble() {
        DoubleSingleValueSeries s = new DoubleSingleValueSeries(1., 2);
        assertEquals(1, s.getDouble(0));
        assertEquals(1, s.getDouble(1));
    }

}
