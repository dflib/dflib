package org.dflib.series;

import org.dflib.Series;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class SingleValueSeries_DiffTest {

    @Test
    public void withEmpty() {
        SingleValueSeries<String> s = new SingleValueSeries<>("a", 5);
        assertSame(s, s.diff(Series.of()));
    }

    @Test
    public void withSelf() {
        SingleValueSeries<String> s = new SingleValueSeries<>("a", 5);
        new SeriesAsserts(s.diff(s)).expectData();
    }

    @Test
    public void diff() {
        SingleValueSeries<String> s = new SingleValueSeries<>("a", 5);

        Series<String> s1 = Series.of(null, "a", "b");
        new SeriesAsserts(s.diff(s1)).expectData();

        Series<String> s2 = Series.of(null, "c", "b");
        new SeriesAsserts(s.diff(s2)).expectData("a", "a", "a", "a", "a");
    }
}
