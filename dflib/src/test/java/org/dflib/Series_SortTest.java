package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Comparator;

public class Series_SortTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void comparator(SeriesType type) {
        Series<String> s = type.createSeries("x", "b", "c", "a").sort(Comparator.naturalOrder());
        new SeriesAsserts(s).expectData("a", "b", "c", "x");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void stringSorter(SeriesType type) {
        Series<String> s = type.createSeries("x", "b", "c", "a").sort("col(0) desc");
        new SeriesAsserts(s).expectData("x", "c", "b", "a");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void stringSorter_Params(SeriesType type) {
        Series<Integer> s1 = type.createSeries(2, 0).sort("int(0) > ? desc", 1);
        new SeriesAsserts(s1).expectData(2, 0);

        Series<Integer> s2 = type.createSeries(0, 2).sort("int(0) > ? desc", 1);
        new SeriesAsserts(s2).expectData(2, 0);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void sorter(SeriesType type) {
        Series<String> s = type.createSeries("x", "b", "c", "a").sort(Exp.$col(0).desc());
        new SeriesAsserts(s).expectData("x", "c", "b", "a");
    }
}
