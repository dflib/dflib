package com.nhl.dflib;

import com.nhl.dflib.unit.LongSeriesAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

public class LongSeries_SortTest {

    @Test
    public void testSortLong() {
        LongSeries s = LongSeries.forLongs(5L, -1L, 5L, 3L, 28L, 1L).sortLong();
        new LongSeriesAsserts(s).expectData(-1L, 1L, 3L, 5L, 5L, 28L);
    }

    @Test
    public void testSort() {
        Series<Long> s = LongSeries.forLongs(5L, -1L, 5L, 3L, 28L, 1L).sort((l1, l2) -> (int) (l2 - l1));
        new SeriesAsserts(s).expectData(28L, 5L, 5L, 3L, 1L, -1L);
    }
}
