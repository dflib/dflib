package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class Series_TailTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void test(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").tail(2);
        new SeriesAsserts(s).expectData("b", "c");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void zero(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").tail(0);
        new SeriesAsserts(s).expectData();
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void outOfBounds(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").tail(4);
        new SeriesAsserts(s).expectData("a", "b", "c");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void negative(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").tail(-2);
        new SeriesAsserts(s).expectData("a");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void negative_OutOfBounds(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").tail(-4);
        new SeriesAsserts(s).expectData("a", "b", "c");
    }
}
