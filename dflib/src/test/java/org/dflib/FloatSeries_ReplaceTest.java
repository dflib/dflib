package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class FloatSeries_ReplaceTest {

    @Test
    public void replace_positions() {

        Series<Float> s1 = Series.ofFloat(1, 0, 2, -1).replace(
                Series.ofInt(1, 3),
                Series.ofFloat(10, 100));

        new SeriesAsserts(s1).expectData(1f, 10f, 2f, 100f);
    }

    @Test
    public void replace_positions_nulls() {

        Series<Float> s1 = Series.ofFloat(1, 0, 2, -1).replace(
                Series.ofInt(1, 3),
                Series.of(10.f, null));

        new SeriesAsserts(s1).expectData(1f, 10f, 2f, null);
    }

    @Test
    public void replace() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Float> s1 = Series.ofFloat(1.1f, 0, 2.05f, -1.0015f).replace(cond, 5.2f);
        assertInstanceOf(FloatSeries.class, s1);
        new SeriesAsserts(s1).expectData(5.2f, 5.2f, 2.05f, -1.0015f);
    }

    @Test
    public void replace_Null() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Float> s1 = Series.ofFloat(1.1f, 0, 2.05f, -1.0015f).replace(cond, null);
        new SeriesAsserts(s1).expectData(null, null, 2.05f, -1.0015f);
    }

    @Test
    public void replace_SmallerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false);

        Series<Float> s1 = Series.ofFloat(1.1f, 0f, 2.05f, -1.0015f).replace(cond, 5.2f);
        assertInstanceOf(FloatSeries.class, s1);
        new SeriesAsserts(s1).expectData(5.2f, 5.2f, 2.05f, -1.0015f);
    }

    @Test
    public void replaceMap() {

        Series<Float> s1 = Series.ofFloat(1.1f, 0.f, 2.f, -1.01f).replace(Map.of(1.1f, -1.5f, 2.f, 15.f));
        assertInstanceOf(FloatSeries.class, s1);
        new SeriesAsserts(s1).expectData(-1.5f, 0.f, 15.f, -1.01f);

        Series<Float> s2 = Series.ofFloat(1.1f, 0.f, 2.f, -1.01f).replace(Collections.singletonMap(2.f, null));
        new SeriesAsserts(s2).expectData(1.1f, 0.f, null, -1.01f);
    }

    @Test
    public void replaceExcept() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Float> s1 = Series.ofFloat(1.1f, 0, 2.05f, -1.0015f).replaceExcept(cond, 5.2f);
        assertInstanceOf(FloatSeries.class, s1);
        new SeriesAsserts(s1).expectData(1.1f, 0.f, 5.2f, 5.2f);
    }

    @Test
    public void replaceExcept_Null() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Float> s1 = Series.ofFloat(1.1f, 0, 2.05f, -1.0015f).replaceExcept(cond, null);
        new SeriesAsserts(s1).expectData(1.1f, 0.f, null, null);
    }

    @Test
    public void replaceExcept_LargerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false, false, false);

        Series<Float> s1 = Series.ofFloat(1.1f, 0, 2.05f, -1.0015f).replaceExcept(cond, 5.2f);
        assertInstanceOf(FloatSeries.class, s1);
        new SeriesAsserts(s1).expectData(1.1f, 0.f, 5.2f, 5.2f);
    }

    @Test
    public void replaceExcept_SmallerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false);

        Series<Float> s1 = Series.ofFloat(1.1f, 0, 2.05f, -1.0015f).replaceExcept(cond, 5.2f);
        assertInstanceOf(FloatSeries.class, s1);
        new SeriesAsserts(s1).expectData(1.1f, 0.f, 5.2f, 5.2f);
    }

}
