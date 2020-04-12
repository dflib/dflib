package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntSeries_ReplaceTest {

    @Test
    public void testReplace() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<Integer> s1 = IntSeries.forInts(1, 0, 2, -1).replace(cond, 5);
        assertTrue(s1 instanceof IntSeries);
        new SeriesAsserts(s1).expectData(5, 5, 2, -1);
    }

    @Test
    public void testReplace_Null() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<Integer> s1 = IntSeries.forInts(1, 0, 2, -1).replace(cond, null);
        assertFalse(s1 instanceof IntSeries);
        new SeriesAsserts(s1).expectData(null, null, 2, -1);
    }

    @Test
    public void testReplace_SmallerCondition() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false);

        Series<Integer> s1 = IntSeries.forInts(1, 0, 2, -1).replace(cond, 5);
        assertTrue(s1 instanceof IntSeries);
        new SeriesAsserts(s1).expectData(5, 5, 2, -1);
    }

    @Test
    public void testReplaceNoMatch() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<Integer> s1 = IntSeries.forInts(1, 0, 2, -1).replaceNoMatch(cond, 5);
        assertTrue(s1 instanceof IntSeries);
        new SeriesAsserts(s1).expectData(1, 0, 5, 5);
    }

    @Test
    public void testReplaceNoMatch_Null() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<Integer> s1 = IntSeries.forInts(1, 0, 2, -1).replaceNoMatch(cond, null);
        assertFalse(s1 instanceof IntSeries);
        new SeriesAsserts(s1).expectData(1, 0, null, null);
    }

    @Test
    public void testReplaceNoMatch_LargerCondition() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false, false);

        Series<Integer> s1 = IntSeries.forInts(1, 0, 2, -1).replaceNoMatch(cond, 5);
        assertTrue(s1 instanceof IntSeries);
        new SeriesAsserts(s1).expectData(1, 0, 5, 5);
    }

    @Test
    public void testReplaceNoMatch_SmallerCondition() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false);

        Series<Integer> s1 = IntSeries.forInts(1, 0, 2, -1).replaceNoMatch(cond, 5);
        assertTrue(s1 instanceof IntSeries);
        new SeriesAsserts(s1).expectData(1, 0, 5, 5);
    }

}
