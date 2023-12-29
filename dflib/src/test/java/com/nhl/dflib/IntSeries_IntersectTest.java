package com.nhl.dflib;

import com.nhl.dflib.series.IntArraySeries;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSeries_IntersectTest {

    @Test
    public void withEmpty() {
        IntSeries s = new IntArraySeries(1, 2);
        new SeriesAsserts(s.intersect(Series.of())).expectData();
    }

    @Test
    public void withSelf() {
        IntSeries s = new IntArraySeries(1, 2);
        new SeriesAsserts(s.intersect(s)).expectData(1, 2);
    }

    @Test
    public void diff() {
        IntSeries s1 = new IntArraySeries(5, 6, 7);
        Series<Integer> s2 = Series.of(6, null, 8);
        new SeriesAsserts(s1.intersect(s2)).expectData(6);
    }

    @Test
    public void diffPrimitive() {
        IntSeries s1 = new IntArraySeries(5, 6, 7);
        IntSeries s2 = new IntArraySeries(6, 8);
        new SeriesAsserts(s1.intersect(s2)).expectData(6);
    }
}
