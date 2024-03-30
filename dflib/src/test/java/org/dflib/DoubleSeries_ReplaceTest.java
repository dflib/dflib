package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DoubleSeries_ReplaceTest {

    @Test
    public void replace_positions() {

        Series<Double> s1 = Series.ofDouble(1, 0, 2, -1).replace(
                Series.ofInt(1, 3),
                Series.ofDouble(10, 100));

        new SeriesAsserts(s1).expectData(1., 10., 2., 100.);
    }

    @Test
    public void replace_positions_nulls() {

        Series<Double> s1 = Series.ofDouble(1, 0, 2, -1).replace(
                Series.ofInt(1, 3),
                Series.of(10., null));

        new SeriesAsserts(s1).expectData(1., 10., 2., null);
    }

    @Test
    public void replace() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Double> s1 = Series.ofDouble(1.1, 0, 2.05, -1.0015).replace(cond, 5.2);
        assertInstanceOf(DoubleSeries.class, s1);
        new SeriesAsserts(s1).expectData(5.2, 5.2, 2.05, -1.0015);
    }

    @Test
    public void replace_Null() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Double> s1 = Series.ofDouble(1.1, 0, 2.05, -1.0015).replace(cond, null);
        new SeriesAsserts(s1).expectData(null, null, 2.05, -1.0015);
    }

    @Test
    public void replace_SmallerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false);

        Series<Double> s1 = Series.ofDouble(1.1, 0, 2.05, -1.0015).replace(cond, 5.2);
        assertInstanceOf(DoubleSeries.class, s1);
        new SeriesAsserts(s1).expectData(5.2, 5.2, 2.05, -1.0015);
    }

    @Test
    public void replaceMap() {

        Series<Double> s1 = Series.ofDouble(1.1, 0., 2., -1.01).replace(Map.of(1.1, -1.5, 2., 15.));
        assertInstanceOf(DoubleSeries.class, s1);
        new SeriesAsserts(s1).expectData(-1.5, 0., 15., -1.01);

        Series<Double> s2 = Series.ofDouble(1.1, 0., 2., -1.01).replace(Collections.singletonMap(2., null));
        new SeriesAsserts(s2).expectData(1.1, 0., null, -1.01);
    }

    @Test
    public void replaceExcept() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Double> s1 = Series.ofDouble(1.1, 0, 2.05, -1.0015).replaceExcept(cond, 5.2);
        assertInstanceOf(DoubleSeries.class, s1);
        new SeriesAsserts(s1).expectData(1.1, 0., 5.2, 5.2);
    }

    @Test
    public void replaceExcept_Null() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<Double> s1 = Series.ofDouble(1.1, 0, 2.05, -1.0015).replaceExcept(cond, null);
        new SeriesAsserts(s1).expectData(1.1, 0., null, null);
    }

    @Test
    public void replaceExcept_LargerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false, false, false);

        Series<Double> s1 = Series.ofDouble(1.1, 0, 2.05, -1.0015).replaceExcept(cond, 5.2);
        assertInstanceOf(DoubleSeries.class, s1);
        new SeriesAsserts(s1).expectData(1.1, 0., 5.2, 5.2);
    }

    @Test
    public void replaceExcept_SmallerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false);

        Series<Double> s1 = Series.ofDouble(1.1, 0, 2.05, -1.0015).replaceExcept(cond, 5.2);
        assertInstanceOf(DoubleSeries.class, s1);
        new SeriesAsserts(s1).expectData(1.1, 0., 5.2, 5.2);
    }

}
