package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LongSeries_ReplaceTest {

    @Test
    public void replace_positions() {

        Series<Long> s1 = Series.ofLong(1, 0, 2, -1).replace(
                Series.ofInt(1, 3),
                Series.ofLong(10, 100));

        new SeriesAsserts(s1).expectData(1L, 10L, 2L, 100L);
    }

    @Test
    public void replace_positions_nulls() {

        Series<Long> s1 = Series.ofLong(1, 0, 2, -1).replace(
                Series.ofInt(1, 3),
                Series.of(10L, null));

        new SeriesAsserts(s1).expectData(1L, 10L, 2L, null);
    }

    @Test
    public void replace() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Long> s1 = Series.ofLong(1, 0, 2, -1).replace(cond, 5L);
        assertInstanceOf(LongSeries.class, s1);
        new SeriesAsserts(s1).expectData(5L, 5L, 2L, -1L);
    }

    @Test
    public void replace_Null() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Long> s1 = Series.ofLong(1, 0, 2, -1).replace(cond, null);
        assertFalse(s1 instanceof LongSeries);
        new SeriesAsserts(s1).expectData(null, null, 2L, -1L);
    }

    @Test
    public void replace_SmallerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false);

        Series<Long> s1 = Series.ofLong(1, 0, 2, -1).replace(cond, 5L);
        assertInstanceOf(LongSeries.class, s1);
        new SeriesAsserts(s1).expectData(5L, 5L, 2L, -1L);
    }

    @Test
    public void replaceMap() {

        Series<Long> s1 = Series.ofLong(1L, 0L, 2L, -1L).replace(Map.of(1L, -1L, 2L, 15L));
        assertInstanceOf(LongSeries.class, s1);
        new SeriesAsserts(s1).expectData(-1L, 0L, 15L, -1L);

        Series<Long> s2 = Series.ofLong(1L, 0L, 2L, -1L).replace(Collections.singletonMap(2L, null));
        new SeriesAsserts(s2).expectData(1L, 0L, null, -1L);
    }

    @Test
    public void replaceExcept() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Long> s1 = Series.ofLong(1, 0, 2, -1).replaceExcept(cond, 5L);
        assertInstanceOf(LongSeries.class, s1);
        new SeriesAsserts(s1).expectData(1L, 0L, 5L, 5L);
    }

    @Test
    public void replaceExcept_Null() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Long> s1 = Series.ofLong(1, 0, 2, -1).replaceExcept(cond, null);
        assertFalse(s1 instanceof LongSeries);
        new SeriesAsserts(s1).expectData(1L, 0L, null, null);
    }

    @Test
    public void replaceExcept_LargerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false, false, false);

        Series<Long> s1 = Series.ofLong(1, 0, 2, -1).replaceExcept(cond, 5L);
        assertInstanceOf(LongSeries.class, s1);
        new SeriesAsserts(s1).expectData(1L, 0L, 5L, 5L);
    }

    @Test
    public void replaceExcept_SmallerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false);

        Series<Long> s1 = Series.ofLong(1, 0, 2, -1).replaceExcept(cond, 5L);
        assertInstanceOf(LongSeries.class, s1);
        new SeriesAsserts(s1).expectData(1L, 0L, 5L, 5L);
    }

}
