package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

import static org.junit.Assert.*;

public class IntSeries_FilterTest {

    @Test
    public void testFilter_BooleanCondition() {
        BooleanSeries condition = BooleanSeries.forBooleans(false, true, true);
        Series<Integer> s = IntSeries.forInts(3, 4, 2).filter(condition);
        new SeriesAsserts(s).expectData(4, 2);
        assertTrue(s instanceof IntSeries);
    }

    @Test
    public void testFilter_Predicate() {
        Series<Integer> s = IntSeries.forInts(3, 4, 2).filter(i -> i > 2);
        new SeriesAsserts(s).expectData(3, 4);
        assertTrue(s instanceof IntSeries);
    }

    @Test
    public void testFilterInt() {
        Series<Integer> s = IntSeries.forInts(3, 4, 2).filterInt(i -> i > 2);
        new SeriesAsserts(s).expectData(3, 4);
        assertTrue(s instanceof IntSeries);
    }
}
