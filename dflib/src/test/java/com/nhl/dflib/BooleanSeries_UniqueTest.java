package com.nhl.dflib;

import com.nhl.dflib.unit.BooleanSeriesAsserts;
import org.junit.Test;

public class BooleanSeries_UniqueTest {

    @Test
    public void test() {
        BooleanSeries s1 = BooleanSeries.forBooleans(true, false, true, false, true).uniqueBoolean();
        new BooleanSeriesAsserts(s1).expectData(true, false);
    }

    @Test
    public void testTrueOnly() {
        BooleanSeries s1 = BooleanSeries.forBooleans(true, true, true).uniqueBoolean();
        new BooleanSeriesAsserts(s1).expectData(true);
    }

    @Test
    public void testFalseOnly() {
        BooleanSeries s1 = BooleanSeries.forBooleans(false, false, false).uniqueBoolean();
        new BooleanSeriesAsserts(s1).expectData(false);
    }

    @Test
    public void testSmall() {
        BooleanSeries s1 = BooleanSeries.forBooleans(false, true).uniqueBoolean();
        new BooleanSeriesAsserts(s1).expectData(false, true);
    }
}
