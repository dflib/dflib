package com.nhl.dflib;

import com.nhl.dflib.unit.LongSeriesAsserts;
import org.junit.jupiter.api.Test;

public class LongSeries_UniqueTest {

    @Test
    public void test() {
        LongSeries s1 = Series.ofLong(0, -1, -1, 0, 1, 375, Long.MAX_VALUE, 5, Long.MAX_VALUE).unique();
        new LongSeriesAsserts(s1).expectData(0, -1, 1, 375, Long.MAX_VALUE, 5);
    }

    @Test
    public void testAlreadyUnique() {
        LongSeries s1 = Series.ofLong(0, -1, 1, 375, Integer.MAX_VALUE, 5).unique();
        new LongSeriesAsserts(s1).expectData(0, -1, 1, 375, Integer.MAX_VALUE, 5);
    }

    @Test
    public void testSmall() {
        LongSeries s1 = Series.ofLong(-1).unique();
        new LongSeriesAsserts(s1).expectData(-1);
    }
}
