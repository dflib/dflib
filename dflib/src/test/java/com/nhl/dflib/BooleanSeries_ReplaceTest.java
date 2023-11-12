package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BooleanSeries_ReplaceTest {

    @Test
    public void replace() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Boolean> s1 = Series.ofBool(true, false, true, true).replace(cond, false);
        assertTrue(s1 instanceof BooleanSeries);
        new SeriesAsserts(s1).expectData(false, false, true, true);

        Series<Boolean> s2 = Series.ofBool(true, false, true, true).replace(cond, true);
        assertTrue(s2 instanceof BooleanSeries);
        new SeriesAsserts(s2).expectData(true, true, true, true);
    }

    @Test
    public void replace_Null() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Boolean> s1 = Series.ofBool(true, false, true, true).replace(cond, null);
        assertFalse(s1 instanceof BooleanSeries);
        new SeriesAsserts(s1).expectData(null, null, true, true);
    }

    @Test
    public void replace_SmallerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false);

        Series<Boolean> s1 = Series.ofBool(true, false, true, true).replace(cond, false);
        assertTrue(s1 instanceof BooleanSeries);
        new SeriesAsserts(s1).expectData(false, false, true, true);

        Series<Boolean> s2 = Series.ofBool(true, false, true, true).replace(cond, true);
        assertTrue(s2 instanceof BooleanSeries);
        new SeriesAsserts(s2).expectData(true, true, true, true);
    }

    @Test
    public void replaceNoMatch() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Boolean> s1 = Series.ofBool(true, false, true, true).replaceNoMatch(cond, false);
        Assertions.assertTrue(s1 instanceof BooleanSeries);
        new SeriesAsserts(s1).expectData(true, false, false, false);

        Series<Boolean> s2 = Series.ofBool(true, false, true, true).replaceNoMatch(cond, true);
        assertTrue(s2 instanceof BooleanSeries);
        new SeriesAsserts(s2).expectData(true, false, true, true);
    }

    @Test
    public void replaceNoMatch_Null() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Boolean> s1 = Series.ofBool(true, false, true, true).replaceNoMatch(cond, null);
        assertFalse(s1 instanceof BooleanSeries);
        new SeriesAsserts(s1).expectData(true, false, null, null);
    }

    @Test
    public void replaceNoMatch_LargerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false, false, false);

        Series<Boolean> s1 = Series.ofBool(true, false, true, true).replaceNoMatch(cond, false);
        assertTrue(s1 instanceof BooleanSeries);
        new SeriesAsserts(s1).expectData(true, false, false, false);

        Series<Boolean> s2 = Series.ofBool(true, false, true, true).replaceNoMatch(cond, true);
        assertTrue(s2 instanceof BooleanSeries);
        new SeriesAsserts(s2).expectData(true, false, true, true);
    }

    @Test
    public void replaceNoMatch_SmallerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false);

        Series<Boolean> s1 = Series.ofBool(true, false, true, true).replaceNoMatch(cond, false);
        assertTrue(s1 instanceof BooleanSeries);
        new SeriesAsserts(s1).expectData(true, false, false, false);

        Series<Boolean> s2 = Series.ofBool(true, false, true, true).replaceNoMatch(cond, true);
        assertTrue(s2 instanceof BooleanSeries);
        new SeriesAsserts(s2).expectData(true, false, true, true);
    }
}
