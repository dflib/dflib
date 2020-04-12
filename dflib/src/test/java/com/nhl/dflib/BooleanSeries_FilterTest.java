package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BooleanSeries_FilterTest {

    @Test
    public void testFilter_BooleanCondition() {
        BooleanSeries condition = BooleanSeries.forBooleans(false, true, true);
        Series<Boolean> s = BooleanSeries.forBooleans(true, false, true).filter(condition);
        new SeriesAsserts(s).expectData(false, true);
        Assertions.assertTrue(s instanceof BooleanSeries);
    }
}
