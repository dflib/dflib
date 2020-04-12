package com.nhl.dflib;

import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Comparator;

public class Series_SortIndexTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void test(SeriesType type) {
        IntSeries s = type.createSeries("x", "b", "c", "a").sortIndex(Comparator.naturalOrder());
        new IntSeriesAsserts(s).expectData(3, 1, 2, 0);
    }
}