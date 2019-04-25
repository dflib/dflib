package com.nhl.dflib;

import com.nhl.dflib.series.LongArraySeries;
import com.nhl.dflib.unit.LongSeriesAsserts;
import org.junit.Test;

public class LongSeries_SelectLongTest {

    @Test
    public void test() {
        LongSeries s = new LongArraySeries(3, 4, 2).selectLong(2, 1);
        new LongSeriesAsserts(s).expectData(2, 4);
    }

    @Test
    public void test_Empty() {
        LongSeries s = new LongArraySeries(3, 4, 2).selectLong();
        new LongSeriesAsserts(s).expectData();
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void test_OutOfBounds() {
        LongSeries s = new LongArraySeries(3, 4, 2).selectLong(0, 3);
        new LongSeriesAsserts(s);
    }
}
