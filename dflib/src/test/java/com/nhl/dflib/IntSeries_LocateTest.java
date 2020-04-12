package com.nhl.dflib;

import com.nhl.dflib.unit.BooleanSeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSeries_LocateTest {

    @Test
    public void testLocate() {
        BooleanSeries s = IntSeries.forInts(3, 4, 2).locate(i -> i.intValue() % 2 == 0);
        new BooleanSeriesAsserts(s).expectData(false, true, true);
    }

    @Test
    public void testLocateInt() {
        BooleanSeries s = IntSeries.forInts(3, 4, 2).locateInt(i -> i % 2 == 0);
        new BooleanSeriesAsserts(s).expectData(false, true, true);
    }
}
