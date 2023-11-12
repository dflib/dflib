package com.nhl.dflib;

import com.nhl.dflib.unit.LongSeriesAsserts;
import org.junit.jupiter.api.Test;

public class LongSeries_SortTest {

    @Test
    public void sortLong() {
        LongSeries s = Series.ofLong(5L, -1L, 5L, 3L, 28L, 1L).sortLong();
        new LongSeriesAsserts(s).expectData(-1L, 1L, 3L, 5L, 5L, 28L);
    }

    @Test
    public void sort_Comparator() {
        LongSeries s = Series.ofLong(5L, -1L, 5L, 3L, 28L, 1L).sort((l1, l2) -> (int) (l2 - l1));
        new LongSeriesAsserts(s).expectData(28L, 5L, 5L, 3L, 1L, -1L);
    }

    @Test
    public void sort_Sorter() {
        LongSeries s = Series.ofLong(5L, -1L, 5L, 3L, 28L, 1L).sort(Exp.$long(0).desc());
        new LongSeriesAsserts(s).expectData(28L, 5L, 5L, 3L, 1L, -1L);
    }
}
