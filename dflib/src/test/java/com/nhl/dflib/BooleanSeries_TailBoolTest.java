package com.nhl.dflib;

import com.nhl.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

public class BooleanSeries_TailBoolTest {

    @Test
    public void test0() {
        BooleanSeries s = Series.ofBool(true, false, true).tailBool(2);
        new BoolSeriesAsserts(s).expectData(false, true);
    }

    @Test
    public void test1() {
        BooleanSeries s = Series.ofBool(true, false, true).tailBool(1);
        new BoolSeriesAsserts(s).expectData(true);
    }

    @Test
    public void test_Zero() {
        BooleanSeries s = Series.ofBool(true, false, true).tailBool(0);
        new BoolSeriesAsserts(s).expectData();
    }

    @Test
    public void test_OutOfBounds() {
        BooleanSeries s = Series.ofBool(true, false, true).tailBool(4);
        new BoolSeriesAsserts(s).expectData(true, false, true);
    }

    @Test
    public void test_Negative() {
        BooleanSeries s = Series.ofBool(true, false, true).tailBool(-2);
        new BoolSeriesAsserts(s).expectData(true);
    }
}
