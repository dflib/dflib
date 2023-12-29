package com.nhl.dflib;

import com.nhl.dflib.series.IntArraySeries;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class IntSeries_DiffTest {

    @Test
    public void withEmpty() {
        IntSeries s = new IntArraySeries(1, 2);
        assertSame(s, s.diff(Series.of()));
    }

    @Test
    public void withSelf() {
        IntSeries s = new IntArraySeries(1, 2);
        new SeriesAsserts(s.diff(s)).expectData();
    }

    @Test
    public void diff() {
        IntSeries s1 = new IntArraySeries(5, 6, 7);
        Series<Integer> s2 = Series.of(6, null, 8);
        new SeriesAsserts(s1.diff(s2)).expectData(5, 7);
    }

    @Test
    public void diffPrimitive() {
        IntSeries s1 = new IntArraySeries(5, 6, 7);
        IntSeries s2 = new IntArraySeries(6, 8);
        new SeriesAsserts(s1.diff(s2)).expectData(5, 7);
    }
}
