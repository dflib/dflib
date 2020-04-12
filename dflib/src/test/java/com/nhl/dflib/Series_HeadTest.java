package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class Series_HeadTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void test(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").head(2);
        new SeriesAsserts(s).expectData("a", "b");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void test_Zero(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").head(0);
        new SeriesAsserts(s).expectData();
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void test_OutOfBounds(SeriesType type) {
        Series<String> s = type.createSeries("a", "b", "c").head(4);
        new SeriesAsserts(s).expectData("a", "b", "c");
    }
}
