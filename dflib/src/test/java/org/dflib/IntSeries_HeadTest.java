package org.dflib;

import org.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSeries_HeadTest {

    @Test
    public void test() {
        IntSeries s = Series.ofInt(3, 4, 2).head(2);
        new IntSeriesAsserts(s).expectData(3, 4);
    }

    @Test
    public void zero() {
        IntSeries s = Series.ofInt(3, 4, 2).head(0);
        new IntSeriesAsserts(s).expectData();
    }

    @Test
    public void outOfBounds() {
        IntSeries s = Series.ofInt(3, 4, 2).head(4);
        new IntSeriesAsserts(s).expectData(3, 4, 2);
    }

    @Test
    public void negative() {
        IntSeries s = Series.ofInt(3, 4, 2).head(-2);
        new IntSeriesAsserts(s).expectData(2);
    }
}
