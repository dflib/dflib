package org.dflib;

import org.dflib.series.LongArraySeries;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class LongSeries_IntersectTest {

    @Test
    public void withEmpty() {
        LongSeries s = new LongArraySeries(1, 2);
        new SeriesAsserts(s.intersect(Series.of())).expectData();
    }

    @Test
    public void withSelf() {
        LongSeries s = new LongArraySeries(1, 2);
        new SeriesAsserts(s.intersect(s)).expectData(1L, 2L);
    }

    @Test
    public void diff() {
        LongSeries s1 = new LongArraySeries(5, 6, 7);
        Series<Long> s2 = Series.of(6L, null, 8L);
        new SeriesAsserts(s1.intersect(s2)).expectData(6L);
    }

    @Test
    public void diffPrimitive() {
        LongSeries s1 = new LongArraySeries(5, 6, 7);
        LongSeries s2 = new LongArraySeries(6, 8);
        new SeriesAsserts(s1.intersect(s2)).expectData(6L);
    }
}
