package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class BooleanSeries_DiffTest {

    @Test
    public void withEmpty() {
        BooleanSeries s = Series.ofBool(true, false);
        assertSame(s, s.diff(Series.of()));
    }

    @Test
    public void withSelf() {
        BooleanSeries s = Series.ofBool(true, false);
        new SeriesAsserts(s.diff(s)).expectData();
    }

    @Test
    public void diff() {
        BooleanSeries s1 = Series.ofBool(true, false);
        Series<Boolean> s2 = Series.of(false, false);

        Series<Boolean> c = s1.diff(s2);
        new SeriesAsserts(c).expectData(true);
    }

    @Test
    public void diffPrimitive() {
        BooleanSeries s1 = Series.ofBool(true, false);
        BooleanSeries s2 = Series.ofBool(false, false);

        Series<Boolean> c = s1.diff(s2);
        new SeriesAsserts(c).expectData(true);
    }
}
