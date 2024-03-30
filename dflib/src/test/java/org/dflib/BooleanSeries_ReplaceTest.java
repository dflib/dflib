package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BooleanSeries_ReplaceTest {

    @Test
    public void replace_positions() {

        Series<Boolean> s1 = Series.ofBool(true, false, false, true).replace(
                Series.ofInt(1, 3),
                Series.ofBool(true, false));

        new SeriesAsserts(s1).expectData(true, true, false, false);
    }

    @Test
    public void replace_positions_nulls() {

        Series<Boolean> s1 = Series.ofBool(true, false, false, true).replace(
                Series.ofInt(1, 3),
                Series.of(true, null));

        new SeriesAsserts(s1).expectData(true, true, false, null);
    }

    @Test
    public void replace() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Boolean> s1 = Series.ofBool(true, false, true, true).replace(cond, false);
        assertInstanceOf(BooleanSeries.class, s1);
        new SeriesAsserts(s1).expectData(false, false, true, true);

        Series<Boolean> s2 = Series.ofBool(true, false, true, true).replace(cond, true);
        assertInstanceOf(BooleanSeries.class, s2);
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
        assertInstanceOf(BooleanSeries.class, s1);
        new SeriesAsserts(s1).expectData(false, false, true, true);

        Series<Boolean> s2 = Series.ofBool(true, false, true, true).replace(cond, true);
        assertInstanceOf(BooleanSeries.class, s2);
        new SeriesAsserts(s2).expectData(true, true, true, true);
    }

    @Test
    public void replaceMap() {

        Series<Boolean> s1 = Series.ofBool(true, false, true, true).replace(Map.of(true, false, false, true));
        assertInstanceOf(BooleanSeries.class, s1);
        new SeriesAsserts(s1).expectData(false, true, false, false);

        Series<Boolean> s2 = Series.ofBool(true, false, true, true).replace(Collections.singletonMap(true, null));
        new SeriesAsserts(s2).expectData(null, false, null, null);
    }

    @Test
    public void replaceExcept() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Boolean> s1 = Series.ofBool(true, false, true, true).replaceExcept(cond, false);
        assertInstanceOf(BooleanSeries.class, s1);
        new SeriesAsserts(s1).expectData(true, false, false, false);

        Series<Boolean> s2 = Series.ofBool(true, false, true, true).replaceExcept(cond, true);
        assertInstanceOf(BooleanSeries.class, s2);
        new SeriesAsserts(s2).expectData(true, false, true, true);
    }

    @Test
    public void replaceExcept_Null() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Boolean> s1 = Series.ofBool(true, false, true, true).replaceExcept(cond, null);
        assertFalse(s1 instanceof BooleanSeries);
        new SeriesAsserts(s1).expectData(true, false, null, null);
    }

    @Test
    public void replaceExcept_LargerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false, false, false);

        Series<Boolean> s1 = Series.ofBool(true, false, true, true).replaceExcept(cond, false);
        assertInstanceOf(BooleanSeries.class, s1);
        new SeriesAsserts(s1).expectData(true, false, false, false);

        Series<Boolean> s2 = Series.ofBool(true, false, true, true).replaceExcept(cond, true);
        assertInstanceOf(BooleanSeries.class, s2);
        new SeriesAsserts(s2).expectData(true, false, true, true);
    }

    @Test
    public void replaceExcept_SmallerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false);

        Series<Boolean> s1 = Series.ofBool(true, false, true, true).replaceExcept(cond, false);
        assertInstanceOf(BooleanSeries.class, s1);
        new SeriesAsserts(s1).expectData(true, false, false, false);

        Series<Boolean> s2 = Series.ofBool(true, false, true, true).replaceExcept(cond, true);
        assertInstanceOf(BooleanSeries.class, s2);
        new SeriesAsserts(s2).expectData(true, false, true, true);
    }
}
