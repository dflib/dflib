package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BooleanSeries_ReplaceTest {

    @Test
    public void testReplace() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<Boolean> s1 = BooleanSeries.forBooleans(true, false, true, true).replace(cond, false);
        assertTrue(s1 instanceof BooleanSeries);
        new SeriesAsserts(s1).expectData(false, false, true, true);

        Series<Boolean> s2 = BooleanSeries.forBooleans(true, false, true, true).replace(cond, true);
        assertTrue(s2 instanceof BooleanSeries);
        new SeriesAsserts(s2).expectData(true, true, true, true);
    }

    @Test
    public void testReplace_Null() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<Boolean> s1 = BooleanSeries.forBooleans(true, false, true, true).replace(cond, null);
        assertFalse(s1 instanceof BooleanSeries);
        new SeriesAsserts(s1).expectData(null, null, true, true);
    }

    @Test
    public void testReplace_SmallerCondition() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false);

        Series<Boolean> s1 = BooleanSeries.forBooleans(true, false, true, true).replace(cond, false);
        assertTrue(s1 instanceof BooleanSeries);
        new SeriesAsserts(s1).expectData(false, false, true, true);

        Series<Boolean> s2 = BooleanSeries.forBooleans(true, false, true, true).replace(cond, true);
        assertTrue(s2 instanceof BooleanSeries);
        new SeriesAsserts(s2).expectData(true, true, true, true);
    }

    @Test
    public void testReplaceNoMatch() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<Boolean> s1 = BooleanSeries.forBooleans(true, false, true, true).replaceNoMatch(cond, false);
        Assertions.assertTrue(s1 instanceof BooleanSeries);
        new SeriesAsserts(s1).expectData(true, false, false, false);

        Series<Boolean> s2 = BooleanSeries.forBooleans(true, false, true, true).replaceNoMatch(cond, true);
        assertTrue(s2 instanceof BooleanSeries);
        new SeriesAsserts(s2).expectData(true, false, true, true);
    }

    @Test
    public void testReplaceNoMatch_Null() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<Boolean> s1 = BooleanSeries.forBooleans(true, false, true, true).replaceNoMatch(cond, null);
        assertFalse(s1 instanceof BooleanSeries);
        new SeriesAsserts(s1).expectData(true, false, null, null);
    }

    @Test
    public void testReplaceNoMatch_LargerCondition() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false, false);

        Series<Boolean> s1 = BooleanSeries.forBooleans(true, false, true, true).replaceNoMatch(cond, false);
        assertTrue(s1 instanceof BooleanSeries);
        new SeriesAsserts(s1).expectData(true, false, false, false);

        Series<Boolean> s2 = BooleanSeries.forBooleans(true, false, true, true).replaceNoMatch(cond, true);
        assertTrue(s2 instanceof BooleanSeries);
        new SeriesAsserts(s2).expectData(true, false, true, true);
    }

    @Test
    public void testReplaceNoMatch_SmallerCondition() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false);

        Series<Boolean> s1 = BooleanSeries.forBooleans(true, false, true, true).replaceNoMatch(cond, false);
        assertTrue(s1 instanceof BooleanSeries);
        new SeriesAsserts(s1).expectData(true, false, false, false);

        Series<Boolean> s2 = BooleanSeries.forBooleans(true, false, true, true).replaceNoMatch(cond, true);
        assertTrue(s2 instanceof BooleanSeries);
        new SeriesAsserts(s2).expectData(true, false, true, true);
    }
}
