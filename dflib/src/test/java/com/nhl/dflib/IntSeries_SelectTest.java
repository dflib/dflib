package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

import static org.junit.Assert.*;

public class IntSeries_SelectTest {

    @Test
    public void test() {
        Series<Integer> s = IntSeries.forInts(3, 4, 2).select(2, 1);
        new SeriesAsserts(s).expectData(2, 4);
        assertTrue(s instanceof IntSeries);
    }

    @Test
    public void test_Empty() {
        Series<Integer> s = IntSeries.forInts(3, 4, 2).select();
        new SeriesAsserts(s).expectData();
        assertTrue(s instanceof IntSeries);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void test_OutOfBounds() {
        IntSeries.forInts(3, 4, 2).select(0, 3);
    }

    @Test
    public void testNulls() {
        Series<Integer> s = IntSeries.forInts(3, 4, 2).select(2, 1, -1);
        new SeriesAsserts(s).expectData(2, 4, null);
        assertFalse(s instanceof IntSeries);
    }
}
