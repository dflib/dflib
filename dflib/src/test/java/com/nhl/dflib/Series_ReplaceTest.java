package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

public class Series_ReplaceTest {

    @Test
    public void testReplace() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<String> s1 = Series.forData("a", "b", "n", "c").replace(cond, "X");
        new SeriesAsserts(s1).expectData("X", "X", "n", "c");
    }

    @Test
    public void testReplace_Null() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<String> s1 = Series.forData("a", "b", "n", "c").replace(cond, null);
        new SeriesAsserts(s1).expectData(null, null, "n", "c");
    }

    @Test
    public void testReplace_SmallerCondition() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false);

        Series<String> s1 = Series.forData("a", "b", "n", "c").replace(cond, "X");
        new SeriesAsserts(s1).expectData("X", "X", "n", "c");
    }

    @Test
    public void testReplaceNoMatch() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<String> s1 = Series.forData("a", "b", "n", "c").replaceNoMatch(cond, "X");
        new SeriesAsserts(s1).expectData("a", "b", "X", "X");
    }

    @Test
    public void testReplaceNoMatch_Null() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<String> s1 = Series.forData("a", "b", "n", "c").replaceNoMatch(cond, null);
        new SeriesAsserts(s1).expectData("a", "b", null, null);
    }

    @Test
    public void testReplaceNoMatch_LargerCondition() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false, false);

        Series<String> s1 = Series.forData("a", "b", "n", "c").replaceNoMatch(cond, "X");
        new SeriesAsserts(s1).expectData("a", "b", "X", "X");
    }

    @Test
    public void testReplaceNoMatch_SmallerCondition() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false);

        Series<String> s1 = Series.forData("a", "b", "n", "c").replaceNoMatch(cond, "X");
        new SeriesAsserts(s1).expectData("a", "b", "X", "X");
    }
}
