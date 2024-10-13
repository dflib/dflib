package org.dflib;

import org.dflib.unit.FloatSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$double;

public class FloatSeries_SortTest {

    @Test
    public void sortFloat() {
        FloatSeries s = Series.ofFloat(5f, -1f, 5f, 3f, 28f, 1f).sortFloat();
        new FloatSeriesAsserts(s).expectData(-1f, 1f, 3f, 5f, 5f, 28f);
    }

    @Test
    public void sort_Comparator() {
        FloatSeries s = Series.ofFloat(5f, -1f, 5f, 3f, 28f, 1f).sort((d1, d2) -> Math.round(d2 - d1));
        new FloatSeriesAsserts(s).expectData(28f, 5f, 5f, 3f, 1f, -1f);
    }

    @Test
    public void sort_Sorter() {
        FloatSeries s = Series.ofFloat(5f, -1f, 5f, 3f, 28f, 1f).sort($double(0).desc());
        new FloatSeriesAsserts(s).expectData(28f, 5f, 5f, 3f, 1f, -1f);
    }
}
