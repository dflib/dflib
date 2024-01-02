package org.dflib;

import org.dflib.unit.DoubleSeriesAsserts;
import org.junit.jupiter.api.Test;

public class DoubleSeries_SortTest {

    @Test
    public void sortDouble() {
        DoubleSeries s = Series.ofDouble(5., -1., 5., 3., 28., 1.).sortDouble();
        new DoubleSeriesAsserts(s).expectData(-1., 1., 3., 5., 5., 28.);
    }

    @Test
    public void sort_Comparator() {
        DoubleSeries s = Series.ofDouble(5., -1., 5., 3., 28., 1.).sort((d1, d2) -> (int) Math.round(d2 - d1));
        new DoubleSeriesAsserts(s).expectData(28., 5., 5., 3., 1., -1.);
    }

    @Test
    public void sort_Sorter() {
        DoubleSeries s = Series.ofDouble(5., -1., 5., 3., 28., 1.).sort(Exp.$double(0).desc());
        new DoubleSeriesAsserts(s).expectData(28., 5., 5., 3., 1., -1.);
    }
}
