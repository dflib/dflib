package org.dflib;

import org.dflib.series.FloatArraySeries;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class FloatSeries_IntersectTest {

    @Test
    public void withEmpty() {
        FloatSeries s = new FloatArraySeries(1, 2);
        new SeriesAsserts(s.intersect(Series.of())).expectData();
    }

    @Test
    public void withSelf() {
        FloatSeries s = new FloatArraySeries(1, 2);
        new SeriesAsserts(s.intersect(s)).expectData(1.f, 2.f);
    }

    @Test
    public void intersect() {
        FloatSeries s1 = new FloatArraySeries(5, 6, 7);
        Series<Float> s2 = Series.of(6.f, null, 8.f);
        new SeriesAsserts(s1.intersect(s2)).expectData(6.f);
    }

    @Test
    public void diffPrimitive() {
        FloatSeries s1 = new FloatArraySeries(5, 6, 7);
        FloatSeries s2 = new FloatArraySeries(6, 8);
        new SeriesAsserts(s1.intersect(s2)).expectData(6.f);
    }
}
