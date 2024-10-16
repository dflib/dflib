package org.dflib.series;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BooleanArraySeriesTest {

    @Test
    public void getBoolean() {
        BooleanArraySeries s = new BooleanArraySeries(true, false);
        assertTrue(s.getBool(0));
        assertFalse(s.getBool(1));
    }
}
