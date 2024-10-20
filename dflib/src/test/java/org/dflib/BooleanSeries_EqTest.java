package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class BooleanSeries_EqTest {

    @Test
    public void eq_All() {
        BooleanSeries s1 = Series.ofBool(true, false, true);
        BooleanSeries s2 = Series.ofBool(true, false, true);

        new SeriesAsserts(s1.eq(s2)).expectData(true, true, true);
    }

    @Test
    public void eq_Self() {
        BooleanSeries s = Series.ofBool(true, false, true);
        new SeriesAsserts(s.eq(s)).expectData(true, true, true);
    }

    @Test
    public void eq_Some() {
        BooleanSeries s1 = Series.ofBool(true, false, true);
        BooleanSeries s2 = Series.ofBool(true, true, true);

        new SeriesAsserts(s1.eq(s2)).expectData(true, false, true);
    }

    @Test
    public void eq_Series() {
        BooleanSeries s1 = Series.ofBool(true, false, true);
        Series<Boolean> s2 = Series.of(true, true, true);

        new SeriesAsserts(s1.eq(s2)).expectData(true, false, true);
    }

    @Test
    public void ne_Some() {
        BooleanSeries s1 = Series.ofBool(true, false, true);
        BooleanSeries s2 = Series.ofBool(true, true, true);

        new SeriesAsserts(s1.ne(s2)).expectData(false, true, false);
    }

    @Test
    public void ne_Series() {
        BooleanSeries s1 = Series.ofBool(true, false, true);
        Series<Boolean> s2 = Series.of(true, true, true);

        new SeriesAsserts(s1.ne(s2)).expectData(false, true, false);
    }

    @Test
    public void eq_IncompatibleSeries() {
        BooleanSeries s1 = Series.ofBool(true, false, true);
        LongSeries s2 = Series.ofLong(1L, 2L, 3L);

        new SeriesAsserts(s1.eq(s2)).expectData(false, false, false);
    }
}
