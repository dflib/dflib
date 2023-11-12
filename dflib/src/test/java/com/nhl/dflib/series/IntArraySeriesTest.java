package com.nhl.dflib.series;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntArraySeriesTest {

    @Test
    public void getInt() {
        IntArraySeries s = new IntArraySeries(1, 2);
        assertEquals(1, s.getInt(0));
        assertEquals(2, s.getInt(1));
    }
}
