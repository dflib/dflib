package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class LongSeries_SetTest {

    @Test
    void set() {
        LongSeries s = Series.ofLong(5L, 4L);
        assertSame(s, s.set(1, 4L));

        new SeriesAsserts(s.set(0, 3L)).expectData(3L, 4L);
    }

    @Test
    void set_Nulls() {
        LongSeries s = Series.ofLong(5L, 4L);
        new SeriesAsserts(s.set(0, null)).expectData(null, 4L);
    }

    @Test
    void setInt() {
        LongSeries s = Series.ofLong(5L, 4L);
        assertSame(s, s.setLong(1, 4L));

        new SeriesAsserts(s.setLong(0, 3L)).expectData(3L, 4L);
    }
}