package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class Series_FilterTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testFilter_BooleanCondition(SeriesType type) {
        BooleanSeries condition = BooleanSeries.forBooleans(false, true, true);
        Series<Integer> s = type.createSeries(3, 4, 2).filter(condition);
        new SeriesAsserts(s).expectData(4, 2);
    }
}
