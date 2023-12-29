package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertSame;

public class Series_DiffTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void diff(SeriesType type) {
        Series<String> s1 = type.createSeries("a", null, "b");
        Series<String> s2 = type.createSeries("b", "c", null);

        Series<String> c = s1.diff(s2);
        new SeriesAsserts(c).expectData("a");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void withEmpty(SeriesType type) {
        Series<String> s = type.createSeries("a", "b");
        assertSame(s, s.diff(Series.of()));
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void withSelf(SeriesType type) {
        Series<String> s = type.createSeries("a", "b");
        Series<String> c = s.diff(s);
        new SeriesAsserts(c).expectData();
    }
}

