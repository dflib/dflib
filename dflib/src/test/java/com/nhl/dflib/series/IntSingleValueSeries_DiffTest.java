package com.nhl.dflib.series;

import com.nhl.dflib.Series;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class IntSingleValueSeries_DiffTest {

    @Test
    public void withEmpty() {
        IntSingleValueSeries s = new IntSingleValueSeries(10, 5);
        assertSame(s, s.diff(Series.of()));
    }

    @Test
    public void withSelf() {
        IntSingleValueSeries s = new IntSingleValueSeries(10, 5);
        new SeriesAsserts(s.diff(s)).expectData();
    }

    @Test
    public void withNull() {
        IntSingleValueSeries s = new IntSingleValueSeries(10, 5);
        new SeriesAsserts(s.diff(Series.of(null, null))).expectData(10, 10, 10, 10, 10);
    }

    @Test
    public void diff() {
        IntSingleValueSeries s = new IntSingleValueSeries(10, 5);

        Series<Integer> s1 = Series.of(null, 10, 11);
        new SeriesAsserts(s.diff(s1)).expectData();

        Series<Integer> s2 = Series.of(null, 12, 11);
        new SeriesAsserts(s.diff(s2)).expectData(10, 10, 10, 10, 10);
    }
}
