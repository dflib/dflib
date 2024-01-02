package org.dflib;

import org.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

public class BooleanSeries_UniqueTest {

    @Test
    public void test() {
        BooleanSeries s1 = Series.ofBool(true, false, true, false, true).unique();
        new BoolSeriesAsserts(s1).expectData(true, false);
    }

    @Test
    public void trueOnly() {
        BooleanSeries s1 = Series.ofBool(true, true, true).unique();
        new BoolSeriesAsserts(s1).expectData(true);
    }

    @Test
    public void falseOnly() {
        BooleanSeries s1 = Series.ofBool(false, false, false).unique();
        new BoolSeriesAsserts(s1).expectData(false);
    }

    @Test
    public void small() {
        BooleanSeries s1 = Series.ofBool(false, true).unique();
        new BoolSeriesAsserts(s1).expectData(false, true);
    }
}
