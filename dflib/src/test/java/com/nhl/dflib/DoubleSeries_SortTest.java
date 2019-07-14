package com.nhl.dflib;

import com.nhl.dflib.unit.DoubleSeriesAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

public class DoubleSeries_SortTest {

    @Test
    public void testSortDouble() {
        DoubleSeries s = DoubleSeries.forDoubles(5., -1., 5., 3., 28., 1.).sortDouble();
        new DoubleSeriesAsserts(s).expectData(-1., 1., 3., 5., 5., 28.);
    }

    @Test
    public void testSort() {
        Series<Double> s = DoubleSeries.forDoubles(5., -1., 5., 3., 28., 1.).sort((d1, d2) -> (int) Math.round(d2 - d1));
        new SeriesAsserts(s).expectData(28., 5., 5., 3., 1., -1.);
    }
}
