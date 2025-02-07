package org.dflib;

import org.dflib.unit.FloatSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FloatSeries_InsertTest {

    @Test
    public void insert_empty() {
        Series s0 = Series.ofFloat(2, 33);
        assertSame(s0, s0.insert(1));
    }

    @Test
    public void insert() {
        Series<?> s1 = Series.ofFloat(2, 33).insert(0, 8.1f, "abc");
        new SeriesAsserts(s1).expectData(8.1f, "abc", 2f, 33f);

        Series<?> s2 = Series.ofFloat(2, 33).insert(1, 8.1f, "abc");
        new SeriesAsserts(s2).expectData(2f, 8.1f, "abc", 33f);

        Series<?> s3 = Series.ofFloat(2, 33).insert(2, 8.1f, "abc");
        new SeriesAsserts(s3).expectData(2f, 33f, 8.1f, "abc");
    }

    @Test
    public void insert_LongObjects() {
        Series<?> s1 = Series.ofFloat(2, 33).insert(1, 8.1f, 11f);
        assertTrue(s1 instanceof FloatSeries);
        new FloatSeriesAsserts((FloatSeries) s1).expectData(2f, 8.1f, 11f, 33f);
    }

    @Test
    public void insertInt() {
        FloatSeries s1 = Series.ofFloat(2, 33).insertFloat(0, 8.1f, 11);
        new FloatSeriesAsserts(s1).expectData(8.1f, 11, 2, 33);

        FloatSeries s2 = Series.ofFloat(2, 33).insertFloat(1, 8.1f, 11);
        new FloatSeriesAsserts(s2).expectData(2, 8.1f, 11, 33);

        FloatSeries s3 = Series.ofFloat(2, 33).insertFloat(2, 8.1f, 11);
        new FloatSeriesAsserts(s3).expectData(2, 33, 8.1f, 11);
    }

    @Test
    public void insert_Negative() {
        assertThrows(IllegalArgumentException.class, () -> Series.ofFloat(2, 33).insert(-1, "abc", 4f));
    }

    @Test
    public void insert_PastEnd() {
        assertThrows(IllegalArgumentException.class, () -> Series.ofFloat(2, 33).insert(3, "abc", 4f));
    }
}
