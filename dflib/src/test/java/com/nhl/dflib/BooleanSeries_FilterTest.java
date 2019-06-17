package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

import static org.junit.Assert.*;

public class BooleanSeries_FilterTest {

    @Test
    public void testFilter_BooleanCondition() {
        BooleanSeries condition = BooleanSeries.forBooleans(false, true, true);
        Series<Boolean> s = BooleanSeries.forBooleans(true, false, true).filter(condition);
        new SeriesAsserts(s).expectData(false, true);
        assertTrue(s instanceof BooleanSeries);
    }
}
