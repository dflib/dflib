package org.dflib;

import org.dflib.unit.FloatSeriesAsserts;
import org.junit.jupiter.api.Test;

public class FloatSeries_HeadTest {

    @Test
    public void test() {
        FloatSeries s = Series.ofFloat(3, 4, 2).head(2);
        new FloatSeriesAsserts(s).expectData(3, 4);
    }

    @Test
    public void zero() {
        FloatSeries s = Series.ofFloat(3, 4, 2).head(0);
        new FloatSeriesAsserts(s).expectData();
    }

    @Test
    public void outOfBounds() {
        FloatSeries s = Series.ofFloat(3, 4, 2).head(4);
        new FloatSeriesAsserts(s).expectData(3, 4, 2);
    }

    @Test
    public void negative() {
        FloatSeries s = Series.ofFloat(3, 4, 2).head(-2);
        new FloatSeriesAsserts(s).expectData(2.f);
    }
}
