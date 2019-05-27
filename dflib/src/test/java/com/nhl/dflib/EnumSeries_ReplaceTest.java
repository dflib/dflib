package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

import static org.junit.Assert.*;

public class EnumSeries_ReplaceTest {

    @Test
    public void testReplace() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<X> s1 = Series.forEnums(X.c, X.a, X.b, X.d).replace(cond, X.f);
        assertTrue(s1 instanceof EnumSeries);
        new SeriesAsserts(s1).expectData(X.f, X.f, X.b, X.d);
    }

    @Test
    public void testReplace_Null() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<X> s1 = Series.forEnums(X.c, X.a, X.b, X.d).replace(cond, null);
        assertFalse(s1 instanceof EnumSeries);
        new SeriesAsserts(s1).expectData(null, null, X.b, X.d);
    }

    @Test
    public void testReplace_SmallerCondition() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false);

        Series<X> s1 = Series.forEnums(X.c, X.a, X.b, X.d).replace(cond, X.f);
        assertTrue(s1 instanceof EnumSeries);
        new SeriesAsserts(s1).expectData(X.f, X.f, X.b, X.d);
    }

    @Test
    public void testReplaceNoMatch() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<X> s1 = Series.forEnums(X.c, X.a, X.b, X.d).replaceNoMatch(cond, X.f);
        assertTrue(s1 instanceof EnumSeries);
        new SeriesAsserts(s1).expectData(X.c, X.a, X.f, X.f);
    }

    @Test
    public void testReplaceNoMatch_Null() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<X> s1 = Series.forEnums(X.c, X.a, X.b, X.d).replaceNoMatch(cond, null);
        assertFalse(s1 instanceof EnumSeries);
        new SeriesAsserts(s1).expectData(X.c, X.a, null, null);
    }

    @Test
    public void testReplaceNoMatch_LargerCondition() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false, false);

        Series<X> s1 = Series.forEnums(X.c, X.a, X.b, X.d).replaceNoMatch(cond, X.f);
        assertTrue(s1 instanceof EnumSeries);
        new SeriesAsserts(s1).expectData(X.c, X.a, X.f, X.f);
    }

    @Test
    public void testReplaceNoMatch_SmallerCondition() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false);

        Series<X> s1 = Series.forEnums(X.c, X.a, X.b, X.d).replaceNoMatch(cond, X.f);
        assertTrue(s1 instanceof EnumSeries);
        new SeriesAsserts(s1).expectData(X.c, X.a, X.f, X.f);
    }

    enum X {a, b, c, d, e, f}
}
