package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class Series_FillTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void fillNulls(SeriesType type) {
        Series<Integer> s = type.createSeries(1, null, 5, 8, null).fillNulls(-1);
        new SeriesAsserts(s).expectData(1, -1, 5, 8, -1);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void fillNullsBackwards(SeriesType type) {
        Series<Integer> s = type.createSeries(null, 1, null, 5, 8, null).fillNullsBackwards();
        new SeriesAsserts(s).expectData(1, 1, 5, 5, 8, null);
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void fillNullsForward(SeriesType type) {
        Series<Integer> s = type.createSeries(null, 1, null, 5, 8, null).fillNullsForward();
        new SeriesAsserts(s).expectData(null, 1, 1, 5, 8, 8);
    }
}
