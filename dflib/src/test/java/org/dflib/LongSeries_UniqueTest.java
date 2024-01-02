package org.dflib;

import org.dflib.unit.LongSeriesAsserts;
import org.junit.jupiter.api.Test;

public class LongSeries_UniqueTest {

    @Test
    public void test() {
        LongSeries s1 = Series.ofLong(0, -1, -1, 0, 1, 375, Long.MAX_VALUE, 5, Long.MAX_VALUE).unique();
        new LongSeriesAsserts(s1).expectData(0, -1, 1, 375, Long.MAX_VALUE, 5);
    }

    @Test
    public void alreadyUnique() {
        LongSeries s1 = Series.ofLong(0, -1, 1, 375, Integer.MAX_VALUE, 5).unique();
        new LongSeriesAsserts(s1).expectData(0, -1, 1, 375, Integer.MAX_VALUE, 5);
    }

    @Test
    public void small() {
        LongSeries s1 = Series.ofLong(-1).unique();
        new LongSeriesAsserts(s1).expectData(-1);
    }
}
