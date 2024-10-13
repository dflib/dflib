package org.dflib;

import org.dflib.series.FloatArraySeries;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class FloatSeries_DiffTest {

    @Test
    public void withEmpty() {
        FloatSeries s = new FloatArraySeries(1, 2);
        assertSame(s, s.diff(Series.of()));
    }

    @Test
    public void withSelf() {
        FloatSeries s = new FloatArraySeries(1, 2);
        new SeriesAsserts(s.diff(s)).expectData();
    }

    @Test
    public void diff() {
        FloatSeries s1 = new FloatArraySeries(5, 6, 7);
        Series<Float> s2 = Series.of(6.f, null, 8.f);
        new SeriesAsserts(s1.diff(s2)).expectData(5.f, 7.f);
    }

    @Test
    public void diffPrimitive() {
        FloatSeries s1 = new FloatArraySeries(5, 6, 7);
        FloatSeries s2 = new FloatArraySeries(6, 8);
        new SeriesAsserts(s1.diff(s2)).expectData(5.f, 7.f);
    }
}
