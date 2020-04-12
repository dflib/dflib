package com.nhl.dflib;

import com.nhl.dflib.unit.BooleanSeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

public class Series_LocateTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testLocate(SeriesType type) {
        BooleanSeries evens = type.createSeries(3, 4, 2).locate(i -> i % 2 == 0);
        new BooleanSeriesAsserts(evens).expectData(false, true, true);
    }
}
