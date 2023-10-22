package com.nhl.dflib;

import com.nhl.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

public class BooleanSeries_HeadBoolTest {

    @Test
    public void test() {
        BooleanSeries s = Series.ofBool(true, false, true).headBool(2);
        new BoolSeriesAsserts(s).expectData(true, false);
    }

    @Test
    public void test_Zero() {
        BooleanSeries s = Series.ofBool(true, false, true).headBool(0);
        new BoolSeriesAsserts(s).expectData();
    }

    @Test
    public void test_OutOfBounds() {
        BooleanSeries s = Series.ofBool(true, false, true).headBool(4);
        new BoolSeriesAsserts(s).expectData(true, false, true);
    }

    @Test
    public void test_Negative() {
        BooleanSeries s = Series.ofBool(true, false, true).headBool(-2);
        new BoolSeriesAsserts(s).expectData(true);
    }
}
