package org.dflib;

import org.dflib.unit.LongSeriesAsserts;
import org.junit.jupiter.api.Test;

public class LongSeries_TailTest {

    @Test
    public void tail2() {
        LongSeries s = Series.ofLong(3, 4, 2).tail(2);
        new LongSeriesAsserts(s).expectData(4L, 2L);
    }

    @Test
    public void tail1() {
        LongSeries s = Series.ofLong(3, 4, 2).tail(1);
        new LongSeriesAsserts(s).expectData(2L);
    }

    @Test
    public void zero() {
        LongSeries s = Series.ofLong(3, 4, 2).tail(0);
        new LongSeriesAsserts(s).expectData();
    }

    @Test
    public void outOfBounds() {
        LongSeries s = Series.ofLong(3, 4, 2).tail(4);
        new LongSeriesAsserts(s).expectData(3L, 4L, 2L);
    }

    @Test
    public void negative() {
        LongSeries s = Series.ofLong(3, 4, 2).tail(-2);
        new LongSeriesAsserts(s).expectData(3L);
    }
}
