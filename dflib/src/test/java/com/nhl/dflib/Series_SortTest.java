package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Comparator;

public class Series_SortTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void test_Comparator(SeriesType type) {
        Series<String> s = type.createSeries("x", "b", "c", "a").sort(Comparator.naturalOrder());
        new SeriesAsserts(s).expectData("a", "b", "c", "x");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void test_Sorter(SeriesType type) {
        Series<String> s = type.createSeries("x", "b", "c", "a").sort(Exp.$col(0).desc());
        new SeriesAsserts(s).expectData("x", "c", "b", "a");
    }
}
