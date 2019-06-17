package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

import static org.junit.Assert.*;

public class LongSeries_FilterTest {

    @Test
    public void testFilter_BooleanCondition() {
        BooleanSeries condition = BooleanSeries.forBooleans(false, true, true);
        Series<Long> s = LongSeries.forLongs(3, 4, 2).filter(condition);
        new SeriesAsserts(s).expectData(4L, 2L);
        assertTrue(s instanceof LongSeries);
    }
}
