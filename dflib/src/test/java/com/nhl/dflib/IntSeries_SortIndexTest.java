package com.nhl.dflib;

import com.nhl.dflib.unit.IntSeriesAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

public class IntSeries_SortIndexTest {

    @Test
    public void testSortIndexInt() {
        IntSeries s = IntSeries.forInts(5, -1, 5, 3, 28, 1).sortIndexInt();
        new IntSeriesAsserts(s).expectData(1, 5, 3, 0, 2, 4);
    }

    @Test
    public void testSortIndexInt_Comparator() {
        IntSeries s = IntSeries.forInts(5, -1, 5, 3, 28, 1).sortIndexInt((i1, i2) -> i2 - i1);
        new IntSeriesAsserts(s).expectData(4, 0, 2, 3, 5, 1);
    }

    @Test
    public void testSortIndex() {
        Series<Integer> s = IntSeries.forInts(5, -1, 5, 3, 28, 1).sortIndex((i1, i2) -> i2 - i1);
        new SeriesAsserts(s).expectData(4, 0, 2, 3, 5, 1);
    }
}
