package org.dflib;

import org.dflib.unit.LongSeriesAsserts;
import org.junit.jupiter.api.Test;

public class LongSeries_AddTest {

    @Test
    public void add_Series() {

        // using a longer series to test optional vectorization extensions
        LongSeries s0 = Series.ofLong(1, 2, 3, 4, 5, 6);
        LongSeries s = Series.ofLong(3, 28, 15, -4, 3, 11).add(s0);
        new LongSeriesAsserts(s).expectData(4L, 30L, 18L, 0L, 8L, 17L);
    }

    @Test
    public void add_Value() {

        LongSeries s = Series.ofLong(3, 28, 15, -4, 3, 11).add(10);
        new LongSeriesAsserts(s).expectData(13L, 38L, 25L, 6L, 13L, 21L);
    }
}
