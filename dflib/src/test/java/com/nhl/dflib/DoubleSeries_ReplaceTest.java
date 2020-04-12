package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DoubleSeries_ReplaceTest {

    @Test
    public void testReplace() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<Double> s1 = DoubleSeries.forDoubles(1.1, 0, 2.05, -1.0015).replace(cond, 5.2);
        assertTrue(s1 instanceof DoubleSeries);
        new SeriesAsserts(s1).expectData(5.2, 5.2, 2.05, -1.0015);
    }

    @Test
    public void testReplace_Null() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<Double> s1 = DoubleSeries.forDoubles(1.1, 0, 2.05, -1.0015).replace(cond, null);
        assertFalse(s1 instanceof DoubleSeries);
        new SeriesAsserts(s1).expectData(null, null, 2.05, -1.0015);
    }

    @Test
    public void testReplace_SmallerCondition() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false);

        Series<Double> s1 = DoubleSeries.forDoubles(1.1, 0, 2.05, -1.0015).replace(cond, 5.2);
        assertTrue(s1 instanceof DoubleSeries);
        new SeriesAsserts(s1).expectData(5.2, 5.2, 2.05, -1.0015);
    }

    @Test
    public void testReplaceNoMatch() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<Double> s1 = DoubleSeries.forDoubles(1.1, 0, 2.05, -1.0015).replaceNoMatch(cond, 5.2);
        assertTrue(s1 instanceof DoubleSeries);
        new SeriesAsserts(s1).expectData(1.1, 0., 5.2, 5.2);
    }

    @Test
    public void testReplaceNoMatch_Null() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false);

        Series<Double> s1 = DoubleSeries.forDoubles(1.1, 0, 2.05, -1.0015).replaceNoMatch(cond, null);
        assertFalse(s1 instanceof DoubleSeries);
        new SeriesAsserts(s1).expectData(1.1, 0., null, null);
    }

    @Test
    public void testReplaceNoMatch_LargerCondition() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false, false, false);

        Series<Double> s1 = DoubleSeries.forDoubles(1.1, 0, 2.05, -1.0015).replaceNoMatch(cond, 5.2);
        assertTrue(s1 instanceof DoubleSeries);
        new SeriesAsserts(s1).expectData(1.1, 0., 5.2, 5.2);
    }

    @Test
    public void testReplaceNoMatch_SmallerCondition() {
        BooleanSeries cond = BooleanSeries.forBooleans(true, true, false);

        Series<Double> s1 = DoubleSeries.forDoubles(1.1, 0, 2.05, -1.0015).replaceNoMatch(cond, 5.2);
        assertTrue(s1 instanceof DoubleSeries);
        new SeriesAsserts(s1).expectData(1.1, 0., 5.2, 5.2);
    }

}
