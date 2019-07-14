package com.nhl.dflib;

import com.nhl.dflib.unit.IntSeriesAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

public class IntSeries_SortTest {

    @Test
    public void testSortInt() {
        IntSeries s = IntSeries.forInts(5, -1, 5, 3, 28, 1).sortInt();
        new IntSeriesAsserts(s).expectData(-1, 1, 3, 5, 5, 28);
    }

    @Test
    public void testSortInt_Comparator() {
        IntSeries s = IntSeries.forInts(5, -1, 5, 3, 28, 1).sortInt((i1, i2) -> i2 - i1);
        new IntSeriesAsserts(s).expectData(28, 5, 5, 3, 1, -1);
    }

    @Test
    public void testSort() {
        Series<Integer> s = IntSeries.forInts(5, -1, 5, 3, 28, 1).sort((i1, i2) -> i2 - i1);
        new SeriesAsserts(s).expectData(28, 5, 5, 3, 1, -1);
    }
}
