package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

public class Series_UnionLongTest {

    @Test
    public void none() {
        new SeriesAsserts(Series.unionLong()).expectData();
    }

    @Test
    public void one() {
        LongSeries s = Series.ofLong(1, 2);
        assertSame(s, Series.unionLong(s));
    }

    @Test
    public void repeated() {
        LongSeries s = Series.ofLong(1, 2);
        LongSeries c = Series.unionLong(s, s);
        new SeriesAsserts(c).expectData(1L, 2L, 1L, 2L);
    }

    @Test
    public void many() {
        LongSeries s1 = Series.ofLong(1, 2);
        LongSeries s2 = Series.ofLong(-1, -2);
        LongSeries s3 = Series.ofLong(0);

        LongSeries c = Series.unionLong(s1, s2, s3);
        new SeriesAsserts(c).expectData(1L, 2L, -1L, -2L, 0L);
    }

    @Test
    public void manyIterable() {
        LongSeries s1 = Series.ofLong(1, 2);
        LongSeries s2 = Series.ofLong(-1, -2);
        LongSeries s3 = Series.ofLong(0);

        LongSeries c = Series.unionLong(List.of(s1, s2, s3));
        new SeriesAsserts(c).expectData(1L, 2L, -1L, -2L, 0L);
    }
}
