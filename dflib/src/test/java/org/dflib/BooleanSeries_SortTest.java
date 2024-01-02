package org.dflib;

import org.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

public class BooleanSeries_SortTest {

    @Test
    public void sort_Comparator() {
        BooleanSeries s = Series.ofBool(true, false, true, false)
                .sort((b1, b2) -> b1 == b2 ? 0 : b1 ? -1 : 1);

        new BoolSeriesAsserts(s).expectData(true, true, false, false);
    }

    @Test
    public void sort_Sorter() {
        BooleanSeries s = Series.ofBool(true, false, true, false)
                .sort(Exp.$bool(0).desc());

        new BoolSeriesAsserts(s).expectData(true, true, false, false);
    }
}
