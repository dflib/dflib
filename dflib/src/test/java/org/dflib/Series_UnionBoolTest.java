package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

public class Series_UnionBoolTest {

    @Test
    public void none() {
        new SeriesAsserts(Series.unionBool()).expectData();
    }

    @Test
    public void one() {
        BooleanSeries s = Series.ofBool(true, false);
        assertSame(s, Series.unionBool(s));
    }

    @Test
    public void repeated() {
        BooleanSeries s = Series.ofBool(true, false);
        BooleanSeries c = Series.unionBool(s, s);
        new SeriesAsserts(c).expectData(true, false, true, false);
    }

    @Test
    public void many() {
        BooleanSeries s1 = Series.ofBool(true, false);
        BooleanSeries s2 = Series.ofBool(false, false);
        BooleanSeries s3 = Series.ofBool(true, true, true);

        BooleanSeries c = Series.unionBool(s1, s2, s3);
        new SeriesAsserts(c).expectData(true, false, false, false, true, true, true);
    }

    @Test
    public void manyIterable() {
        BooleanSeries s1 = Series.ofBool(true, false);
        BooleanSeries s2 = Series.ofBool(false, false);
        BooleanSeries s3 = Series.ofBool(true, true, true);

        BooleanSeries c = Series.unionBool(List.of(s1, s2, s3));
        new SeriesAsserts(c).expectData(true, false, false, false, true, true, true);
    }
}
