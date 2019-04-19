package com.nhl.dflib;

import com.nhl.dflib.series.BooleanArraySeries;
import com.nhl.dflib.unit.BooleanSeriesAsserts;
import org.junit.Test;

public class BooleanSeries_SelectBooleanTest {

    @Test
    public void test() {
        BooleanSeries s = new BooleanArraySeries(true, false, true).selectBoolean(2, 1);
        new BooleanSeriesAsserts(s).expectData(true, false);
    }

    @Test
    public void test_Empty() {
        BooleanSeries s = new BooleanArraySeries(true, false, true).selectBoolean();
        new BooleanSeriesAsserts(s).expectData();
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void test_OutOfBounds() {
        BooleanSeries s = new BooleanArraySeries(true, false, true).selectBoolean(0, 3);
        new BooleanSeriesAsserts(s);
    }
}
