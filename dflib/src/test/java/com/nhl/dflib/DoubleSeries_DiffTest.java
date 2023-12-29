package com.nhl.dflib;

import com.nhl.dflib.series.DoubleArraySeries;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class DoubleSeries_DiffTest {

    @Test
    public void withEmpty() {
        DoubleSeries s = new DoubleArraySeries(1, 2);
        assertSame(s, s.diff(Series.of()));
    }

    @Test
    public void withSelf() {
        DoubleSeries s = new DoubleArraySeries(1, 2);
        new SeriesAsserts(s.diff(s)).expectData();
    }

    @Test
    public void diff() {
        DoubleSeries s1 = new DoubleArraySeries(5, 6, 7);
        Series<Double> s2 = Series.of(6., null, 8.);
        new SeriesAsserts(s1.diff(s2)).expectData(5., 7.);
    }

    @Test
    public void diffPrimitive() {
        DoubleSeries s1 = new DoubleArraySeries(5, 6, 7);
        DoubleSeries s2 = new DoubleArraySeries(6, 8);
        new SeriesAsserts(s1.diff(s2)).expectData(5., 7.);
    }
}
