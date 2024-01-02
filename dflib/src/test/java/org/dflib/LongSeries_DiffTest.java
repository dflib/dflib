package org.dflib;

import org.dflib.series.LongArraySeries;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class LongSeries_DiffTest {

    @Test
    public void withEmpty() {
        LongSeries s = new LongArraySeries(1, 2);
        assertSame(s, s.diff(Series.of()));
    }

    @Test
    public void withSelf() {
        LongSeries s = new LongArraySeries(1, 2);
        new SeriesAsserts(s.diff(s)).expectData();
    }

    @Test
    public void diff() {
        LongSeries s1 = new LongArraySeries(5, 6, 7);
        Series<Long> s2 = Series.of(6L, null, 8L);
        new SeriesAsserts(s1.diff(s2)).expectData(5L, 7L);
    }

    @Test
    public void diffPrimitive() {
        LongSeries s1 = new LongArraySeries(5, 6, 7);
        LongSeries s2 = new LongArraySeries(6, 8);
        new SeriesAsserts(s1.diff(s2)).expectData(5L, 7L);
    }
}
