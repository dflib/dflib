package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LongSeries_ReplaceTest {

    @Test
    public void testReplace() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<Long> s1 = LongSeries.forLongs(1, 0, 2, -1).replace(cond, 5L);
        assertTrue(s1 instanceof LongSeries);
        new SeriesAsserts(s1).expectData(5L, 5L, 2L, -1L);
    }

    @Test
    public void testReplace_Null() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<Long> s1 = LongSeries.forLongs(1, 0, 2, -1).replace(cond, null);
        assertFalse(s1 instanceof LongSeries);
        new SeriesAsserts(s1).expectData(null, null, 2L, -1L);
    }

    @Test
    public void testReplace_SmallerCondition() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false);

        Series<Long> s1 = LongSeries.forLongs(1, 0, 2, -1).replace(cond, 5L);
        assertTrue(s1 instanceof LongSeries);
        new SeriesAsserts(s1).expectData(5L, 5L, 2L, -1L);
    }

    @Test
    public void testReplaceNoMatch() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<Long> s1 = LongSeries.forLongs(1, 0, 2, -1).replaceNoMatch(cond, 5L);
        assertTrue(s1 instanceof LongSeries);
        new SeriesAsserts(s1).expectData(1L, 0L, 5L, 5L);
    }

    @Test
    public void testReplaceNoMatch_Null() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<Long> s1 = LongSeries.forLongs(1, 0, 2, -1).replaceNoMatch(cond, null);
        assertFalse(s1 instanceof LongSeries);
        new SeriesAsserts(s1).expectData(1L, 0L, null, null);
    }

    @Test
    public void testReplaceNoMatch_LargerCondition() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false, false);

        Series<Long> s1 = LongSeries.forLongs(1, 0, 2, -1).replaceNoMatch(cond, 5L);
        assertTrue(s1 instanceof LongSeries);
        new SeriesAsserts(s1).expectData(1L, 0L, 5L, 5L);
    }

    @Test
    public void testReplaceNoMatch_SmallerCondition() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false);

        Series<Long> s1 = LongSeries.forLongs(1, 0, 2, -1).replaceNoMatch(cond, 5L);
        assertTrue(s1 instanceof LongSeries);
        new SeriesAsserts(s1).expectData(1L, 0L, 5L, 5L);
    }

}
