package org.dflib;

import org.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

public class BooleanSeries_HeadTest {

    @Test
    public void test() {
        BooleanSeries s = Series.ofBool(true, false, true).head(2);
        new BoolSeriesAsserts(s).expectData(true, false);
    }

    @Test
    public void zero() {
        BooleanSeries s = Series.ofBool(true, false, true).head(0);
        new BoolSeriesAsserts(s).expectData();
    }

    @Test
    public void outOfBounds() {
        BooleanSeries s = Series.ofBool(true, false, true).head(4);
        new BoolSeriesAsserts(s).expectData(true, false, true);
    }

    @Test
    public void negative() {
        BooleanSeries s = Series.ofBool(true, false, true).head(-2);
        new BoolSeriesAsserts(s).expectData(true);
    }
}
