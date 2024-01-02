package org.dflib.series;

import org.dflib.Series;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class SingleValueSeries_IntersectTest {

    @Test
    public void withEmpty() {
        SingleValueSeries<String> s = new SingleValueSeries<>("a", 5);
        new SeriesAsserts(s.intersect(Series.of())).expectData();
    }

    @Test
    public void withSelf() {
        SingleValueSeries<String> s = new SingleValueSeries<>("a", 5);
        new SeriesAsserts(s.intersect(s)).expectData("a", "a", "a", "a", "a");
    }

    @Test
    public void intersect() {
        SingleValueSeries<String> s = new SingleValueSeries<>("a", 5);

        Series<String> s1 = Series.of(null, "a", "b");
        new SeriesAsserts(s.intersect(s1)).expectData("a", "a", "a", "a", "a");

        Series<String> s2 = Series.of(null, "c", "b");
        new SeriesAsserts(s.intersect(s2)).expectData();
    }
}
