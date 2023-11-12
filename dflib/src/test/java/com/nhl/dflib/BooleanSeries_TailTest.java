package com.nhl.dflib;

import com.nhl.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

public class BooleanSeries_TailTest {

    @Test
    public void tail2() {
        BooleanSeries s = Series.ofBool(true, false, true).tail(2);
        new BoolSeriesAsserts(s).expectData(false, true);
    }

    @Test
    public void tail1() {
        BooleanSeries s = Series.ofBool(true, false, true).tail(1);
        new BoolSeriesAsserts(s).expectData(true);
    }

    @Test
    public void zero() {
        BooleanSeries s = Series.ofBool(true, false, true).tail(0);
        new BoolSeriesAsserts(s).expectData();
    }

    @Test
    public void outOfBounds() {
        BooleanSeries s = Series.ofBool(true, false, true).tail(4);
        new BoolSeriesAsserts(s).expectData(true, false, true);
    }

    @Test
    public void negative() {
        BooleanSeries s = Series.ofBool(true, false, true).tail(-2);
        new BoolSeriesAsserts(s).expectData(true);
    }
}
