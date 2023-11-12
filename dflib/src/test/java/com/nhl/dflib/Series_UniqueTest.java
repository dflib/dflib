package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class Series_UniqueTest {

    Object o1 = new Object();
    Object o2 = "__";
    Object o3 = new Integer(9);
    Object o4 = new Integer(9);

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void test(SeriesType type) {
        Series<Object> s1 = type.createSeries(o4, o1, o2, o3, o1).unique();
        new SeriesAsserts(s1).expectData(o4, o1, o2);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void alreadyUnique(SeriesType type) {
        Series<Object> s1 = type.createSeries(o4, o1, o2).unique();
        new SeriesAsserts(s1).expectData(o4, o1, o2);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void small(SeriesType type) {
        Series<Object> s1 = type.createSeries(o4).unique();
        new SeriesAsserts(s1).expectData(o4);
    }
}
