package com.nhl.dflib;

import com.nhl.dflib.unit.LongSeriesAsserts;
import org.junit.jupiter.api.Test;

public class LongSeries_HeadTest {

    @Test
    public void test() {
        LongSeries s = Series.ofLong(3, 4, 2).head(2);
        new LongSeriesAsserts(s).expectData(3, 4);
    }

    @Test
    public void test_Zero() {
        LongSeries s = Series.ofLong(3, 4, 2).head(0);
        new LongSeriesAsserts(s).expectData();
    }

    @Test
    public void test_OutOfBounds() {
        LongSeries s = Series.ofLong(3, 4, 2).head(4);
        new LongSeriesAsserts(s).expectData(3, 4, 2);
    }

    @Test
    public void test_Negative() {
        LongSeries s = Series.ofLong(3, 4, 2).head(-2);
        new LongSeriesAsserts(s).expectData(2);
    }
}
