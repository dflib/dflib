package com.nhl.dflib;

import com.nhl.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

public class BooleanSeries_UniqueTest {

    @Test
    public void test() {
        BooleanSeries s1 = Series.ofBool(true, false, true, false, true).uniqueBool();
        new BoolSeriesAsserts(s1).expectData(true, false);
    }

    @Test
    public void testTrueOnly() {
        BooleanSeries s1 = Series.ofBool(true, true, true).uniqueBool();
        new BoolSeriesAsserts(s1).expectData(true);
    }

    @Test
    public void testFalseOnly() {
        BooleanSeries s1 = Series.ofBool(false, false, false).uniqueBool();
        new BoolSeriesAsserts(s1).expectData(false);
    }

    @Test
    public void testSmall() {
        BooleanSeries s1 = Series.ofBool(false, true).uniqueBool();
        new BoolSeriesAsserts(s1).expectData(false, true);
    }
}
