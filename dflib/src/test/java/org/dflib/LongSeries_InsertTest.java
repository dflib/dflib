package org.dflib;

import org.dflib.unit.LongSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LongSeries_InsertTest {

    @Test
    public void insert_empty() {
        Series s0 = Series.ofLong(2, 33);
        assertSame(s0, s0.insert(1));
    }

    @Test
    public void insert() {
        Series<?> s1 = Series.ofLong(2, 33).insert(0, 8L, "abc");
        new SeriesAsserts(s1).expectData(8L, "abc", 2L, 33L);

        Series<?> s2 = Series.ofLong(2, 33).insert(1, 8L, "abc");
        new SeriesAsserts(s2).expectData(2L, 8L, "abc", 33L);

        Series<?> s3 = Series.ofLong(2, 33).insert(2, 8L, "abc");
        new SeriesAsserts(s3).expectData(2L, 33L, 8L, "abc");
    }

    @Test
    public void insert_LongObjects() {
        Series<?> s1 = Series.ofLong(2, 33).insert(1, 8L, 11L);
        assertTrue(s1 instanceof LongSeries);
        new LongSeriesAsserts((LongSeries) s1).expectData(2L, 8L, 11L, 33L);
    }

    @Test
    public void insertInt() {
        LongSeries s1 = Series.ofLong(2, 33).insertLong(0, 8, 11);
        new LongSeriesAsserts(s1).expectData(8, 11, 2, 33);

        LongSeries s2 = Series.ofLong(2, 33).insertLong(1, 8, 11);
        new LongSeriesAsserts(s2).expectData(2, 8, 11, 33);

        LongSeries s3 = Series.ofLong(2, 33).insertLong(2, 8, 11);
        new LongSeriesAsserts(s3).expectData(2, 33, 8, 11);
    }

    @Test
    public void insert_Negative() {
        assertThrows(IllegalArgumentException.class, () -> Series.ofLong(2, 33).insert(-1, "abc", 4L));
    }

    @Test
    public void insert_PastEnd() {
        assertThrows(IllegalArgumentException.class, () -> Series.ofLong(2, 33).insert(3, "abc", 4L));
    }
}
