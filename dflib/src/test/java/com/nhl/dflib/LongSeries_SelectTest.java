package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LongSeries_SelectTest {

    @Test
    public void testPositional() {
        Series<Long> s = LongSeries.forLongs(3, 4, 2).select(2, 1);
        new SeriesAsserts(s).expectData(2L, 4L);
        assertTrue(s instanceof LongSeries);
    }

    @Test
    public void testPositional_Empty() {
        Series<Long> s = LongSeries.forLongs(3, 4, 2).select();
        new SeriesAsserts(s).expectData();
        assertTrue(s instanceof LongSeries);
    }

    @Test
    public void tesPositionalt_OutOfBounds() {
        LongSeries s = LongSeries.forLongs(3, 4, 2);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> s.select(0, 3));
    }

    @Test
    public void testPositionalNulls() {
        Series<Long> s = LongSeries.forLongs(3, 4, 2).select(2, 1, -1);
        new SeriesAsserts(s).expectData(2L, 4L, null);
        assertFalse(s instanceof LongSeries);
    }
}
