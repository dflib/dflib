package org.dflib;

import org.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSeries_LocateTest {

    @Test
    public void locate() {
        BooleanSeries s = Series.ofInt(3, 4, 2).locate(i -> i.intValue() % 2 == 0);
        new BoolSeriesAsserts(s).expectData(false, true, true);
    }

    @Test
    public void locateInt() {
        BooleanSeries s = Series.ofInt(3, 4, 2).locateInt(i -> i % 2 == 0);
        new BoolSeriesAsserts(s).expectData(false, true, true);
    }
}
