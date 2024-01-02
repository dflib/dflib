package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class Series_IntersectTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void intersect(SeriesType type) {
        Series<String> s1 = type.createSeries("a", null, "b");
        Series<String> s2 = type.createSeries("b", "c", null);

        Series<String> c = s1.intersect(s2);
        new SeriesAsserts(c).expectData(null, "b");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void withEmpty(SeriesType type) {
        Series<String> s = type.createSeries("a", "b");
        new SeriesAsserts(s.intersect(Series.of())).expectData();
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void withSelf(SeriesType type) {
        Series<String> s = type.createSeries("a", "b");
        new SeriesAsserts(s.intersect(s)).expectData("a", "b");
    }
}

