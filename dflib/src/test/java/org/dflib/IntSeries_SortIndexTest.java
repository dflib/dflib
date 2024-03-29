package org.dflib;

import org.dflib.unit.IntSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSeries_SortIndexTest {

    @Test
    public void sortIndexInt() {
        IntSeries s = Series.ofInt(5, -1, 5, 3, 28, 1).sortIndexInt();
        new IntSeriesAsserts(s).expectData(1, 5, 3, 0, 2, 4);
    }

    @Test
    public void sortIndexInt_Comparator() {
        IntSeries s = Series.ofInt(5, -1, 5, 3, 28, 1).sortIndexInt((i1, i2) -> i2 - i1);
        new IntSeriesAsserts(s).expectData(4, 0, 2, 3, 5, 1);
    }

    @Test
    public void sortIndex() {
        Series<Integer> s = Series.ofInt(5, -1, 5, 3, 28, 1).sortIndex((i1, i2) -> i2 - i1);
        new SeriesAsserts(s).expectData(4, 0, 2, 3, 5, 1);
    }
}
