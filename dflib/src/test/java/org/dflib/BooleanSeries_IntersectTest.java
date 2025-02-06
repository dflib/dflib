package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class BooleanSeries_IntersectTest {

    @Test
    public void withEmpty() {
        BooleanSeries s = Series.ofBool(true, false);
        new SeriesAsserts(s.intersect(Series.of())).expectData();
    }

    @Test
    public void withSelf() {
        BooleanSeries s = Series.ofBool(true, false);
        Series<Boolean> c = s.intersect(s);
        new SeriesAsserts(c).expectData(true, false);
    }

    @Test
    public void intersect() {
        BooleanSeries s1 = Series.ofBool(true, false);
        Series<Boolean> s2 = Series.of(false, false);

        Series<Boolean> c = s1.intersect(s2);
        new SeriesAsserts(c).expectData(false);
    }

    @Test
    public void intersectPrimitive() {
        BooleanSeries s1 = Series.ofBool(true, false);
        BooleanSeries s2 = Series.ofBool(false, false);

        Series<Boolean> c = s1.intersect(s2);
        new SeriesAsserts(c).expectData(false);
    }
}
