package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class Series_ShiftTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void defaultNull(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c", "d").shift(2);
        new SeriesAsserts(s).expectData(null, null, "a", "b");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void positive(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c", "d").shift(2, "X");
        new SeriesAsserts(s).expectData("X", "X", "a", "b");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void positiveOutOfBounds(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c", "d").shift(100, "X");
        new SeriesAsserts(s).expectData("X", "X", "X", "X");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void negative(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c", "d").shift(-2, "X");
        new SeriesAsserts(s).expectData("c", "d", "X", "X");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void negativeOutOfBounds(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c", "d").shift(-100, "X");
        new SeriesAsserts(s).expectData("X", "X", "X", "X");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void zero(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c", "d").shift(0, "X");
        new SeriesAsserts(s).expectData("a", "b", "c", "d");
    }
}
