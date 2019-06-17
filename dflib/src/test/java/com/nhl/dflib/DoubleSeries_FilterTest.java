package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

import static org.junit.Assert.*;

public class DoubleSeries_FilterTest {

    @Test
    public void testFilter_BooleanCondition() {
        BooleanSeries condition = BooleanSeries.forBooleans(false, true, true);
        Series<Double> s = DoubleSeries.forDoubles(3, 4, 2).filter(condition);
        new SeriesAsserts(s).expectData(4., 2.);
        assertTrue(s instanceof DoubleSeries);
    }
}
