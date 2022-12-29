package com.nhl.dflib;

import com.nhl.dflib.unit.BooleanSeriesAsserts;
import org.junit.jupiter.api.Test;

public class BooleanSeries_SortTest {

    @Test
    public void testSort_Comparator() {
        BooleanSeries s = Series.ofBool(true, false, true, false)
                .sort((b1, b2) -> b1 == b2 ? 0 : b1 ? -1 : 1);

        new BooleanSeriesAsserts(s).expectData(true, true, false, false);
    }

    @Test
    public void testSort_Sorter() {
        BooleanSeries s = Series.ofBool(true, false, true, false)
                .sort(Exp.$bool(0).desc());

        new BooleanSeriesAsserts(s).expectData(true, true, false, false);
    }
}
