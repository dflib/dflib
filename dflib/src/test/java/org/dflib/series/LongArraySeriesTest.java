package org.dflib.series;

import org.dflib.series.LongArraySeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LongArraySeriesTest {

    @Test
    public void getLong() {
        LongArraySeries s = new LongArraySeries(1, 2);
        assertEquals(1, s.getLong(0));
        assertEquals(2, s.getLong(1));
    }
}
