package org.dflib.series;

import org.dflib.series.DoubleArraySeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DoubleArraySeriesTest {

    @Test
    public void getDouble() {
        DoubleArraySeries s = new DoubleArraySeries(1., 2.1);
        assertEquals(1, s.getDouble(0), 0.001);
        assertEquals(2.1, s.getDouble(1), 0.001);
    }
}
