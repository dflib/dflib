package com.nhl.dflib;

import com.nhl.dflib.series.DoubleArraySeries;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class DoubleSeries_IntersectTest {

    @Test
    public void withEmpty() {
        DoubleSeries s = new DoubleArraySeries(1, 2);
        new SeriesAsserts(s.intersect(Series.of())).expectData();
    }

    @Test
    public void withSelf() {
        DoubleSeries s = new DoubleArraySeries(1, 2);
        new SeriesAsserts(s.intersect(s)).expectData(1., 2.);
    }

    @Test
    public void diff() {
        DoubleSeries s1 = new DoubleArraySeries(5, 6, 7);
        Series<Double> s2 = Series.of(6., null, 8.);
        new SeriesAsserts(s1.intersect(s2)).expectData(6.);
    }

    @Test
    public void diffPrimitive() {
        DoubleSeries s1 = new DoubleArraySeries(5, 6, 7);
        DoubleSeries s2 = new DoubleArraySeries(6, 8);
        new SeriesAsserts(s1.intersect(s2)).expectData(6.);
    }
}
