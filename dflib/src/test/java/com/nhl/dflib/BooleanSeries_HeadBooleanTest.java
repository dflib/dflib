package com.nhl.dflib;

import com.nhl.dflib.series.BooleanArraySeries;
import com.nhl.dflib.unit.BooleanSeriesAsserts;
import org.junit.Test;

public class BooleanSeries_HeadBooleanTest {

    @Test
    public void test() {
        BooleanSeries s = new BooleanArraySeries(true, false, true).headBoolean(2);
        new BooleanSeriesAsserts(s).expectData(true, false);
    }

    @Test
    public void test_Zero() {
        BooleanSeries s = new BooleanArraySeries(true, false, true).headBoolean(0);
        new BooleanSeriesAsserts(s).expectData();
    }

    @Test
    public void test_OutOfBounds() {
        BooleanSeries s = new BooleanArraySeries(true, false, true).headBoolean(4);
        new BooleanSeriesAsserts(s).expectData(true, false, true);
    }
}
