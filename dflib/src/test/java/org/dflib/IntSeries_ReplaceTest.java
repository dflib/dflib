package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class IntSeries_ReplaceTest {

    @Test
    public void replace_positions() {

        Series<Integer> s1 = Series.ofInt(1, 0, 2, -1).replace(
                Series.ofInt(1, 3),
                Series.ofInt(10, 100));

        new SeriesAsserts(s1).expectData(1, 10, 2, 100);
    }

    @Test
    public void replace_positions_nulls() {

        Series<Integer> s1 = Series.ofInt(1, 0, 2, -1).replace(
                Series.ofInt(1, 3),
                Series.of(10, null));

        new SeriesAsserts(s1).expectData(1, 10, 2, null);
    }

    @Test
    public void replace() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Integer> s1 = Series.ofInt(1, 0, 2, -1).replace(cond, 5);
        assertTrue(s1 instanceof IntSeries);
        new SeriesAsserts(s1).expectData(5, 5, 2, -1);
    }

    @Test
    public void replace_Null() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Integer> s1 = Series.ofInt(1, 0, 2, -1).replace(cond, null);
        assertFalse(s1 instanceof IntSeries);
        new SeriesAsserts(s1).expectData(null, null, 2, -1);
    }

    @Test
    public void replace_SmallerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false);

        Series<Integer> s1 = Series.ofInt(1, 0, 2, -1).replace(cond, 5);
        assertTrue(s1 instanceof IntSeries);
        new SeriesAsserts(s1).expectData(5, 5, 2, -1);
    }

    @Test
    public void replaceMap() {

        Series<Integer> s1 = Series.ofInt(1, 0, 2, -1).replace(Map.of(1, -1, 2, 15));
        assertTrue(s1 instanceof IntSeries);
        new SeriesAsserts(s1).expectData(-1, 0, 15, -1);

        Series<Integer> s2 = Series.ofInt(1, 0, 2, -1).replace(Collections.singletonMap(2, null));
        new SeriesAsserts(s2).expectData(1, 0, null, -1);
    }

    @Test
    public void replaceExcept() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Integer> s1 = Series.ofInt(1, 0, 2, -1).replaceExcept(cond, 5);
        assertTrue(s1 instanceof IntSeries);
        new SeriesAsserts(s1).expectData(1, 0, 5, 5);
    }

    @Test
    public void replaceExcept_Null() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Integer> s1 = Series.ofInt(1, 0, 2, -1).replaceExcept(cond, null);
        assertFalse(s1 instanceof IntSeries);
        new SeriesAsserts(s1).expectData(1, 0, null, null);
    }

    @Test
    public void replaceExcept_LargerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false, false, false);

        Series<Integer> s1 = Series.ofInt(1, 0, 2, -1).replaceExcept(cond, 5);
        assertTrue(s1 instanceof IntSeries);
        new SeriesAsserts(s1).expectData(1, 0, 5, 5);
    }

    @Test
    public void replaceExcept_SmallerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false);

        Series<Integer> s1 = Series.ofInt(1, 0, 2, -1).replaceExcept(cond, 5);
        assertTrue(s1 instanceof IntSeries);
        new SeriesAsserts(s1).expectData(1, 0, 5, 5);
    }

}
