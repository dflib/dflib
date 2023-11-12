package com.nhl.dflib.series;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BooleanArraySeriesTest {

    @Test
    public void getBoolean() {
        BooleanArraySeries s = new BooleanArraySeries(true, false);
        assertEquals(true, s.getBool(0));
        assertEquals(false, s.getBool(1));
    }
}
