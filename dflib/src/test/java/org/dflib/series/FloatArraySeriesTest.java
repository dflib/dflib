package org.dflib.series;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FloatArraySeriesTest {

    @Test
    public void getFloat() {
        FloatArraySeries s = new FloatArraySeries(1.f, 2.1f);
        assertEquals(1f, s.getFloat(0), 0.001f);
        assertEquals(2.1f, s.getFloat(1), 0.001f);
    }
}
