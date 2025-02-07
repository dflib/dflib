package org.dflib;

import org.dflib.unit.DoubleSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DoubleSeries_InsertTest {

    @Test
    public void insert_empty() {
        Series s0 = Series.ofDouble(2, 33);
        assertSame(s0, s0.insert(1));
    }

    @Test
    public void insert() {
        Series<?> s1 = Series.ofDouble(2, 33).insert(0, 8.1d, "abc");
        new SeriesAsserts(s1).expectData(8.1d, "abc", 2d, 33d);

        Series<?> s2 = Series.ofDouble(2, 33).insert(1, 8.1d, "abc");
        new SeriesAsserts(s2).expectData(2d, 8.1d, "abc", 33d);

        Series<?> s3 = Series.ofDouble(2, 33).insert(2, 8.1d, "abc");
        new SeriesAsserts(s3).expectData(2d, 33d, 8.1d, "abc");
    }

    @Test
    public void insert_DoubleObjects() {
        Series<?> s1 = Series.ofDouble(2, 33).insert(1, 8.1d, 11d);
        assertTrue(s1 instanceof DoubleSeries);
        new DoubleSeriesAsserts((DoubleSeries) s1).expectData(2d, 8.1d, 11d, 33d);
    }

    @Test
    public void insertInt() {
        DoubleSeries s1 = Series.ofDouble(2, 33).insertDouble(0, 8.1d, 11);
        new DoubleSeriesAsserts(s1).expectData(8.1d, 11, 2, 33);

        DoubleSeries s2 = Series.ofDouble(2, 33).insertDouble(1, 8.1d, 11);
        new DoubleSeriesAsserts(s2).expectData(2, 8.1d, 11, 33);

        DoubleSeries s3 = Series.ofDouble(2, 33).insertDouble(2, 8.1d, 11);
        new DoubleSeriesAsserts(s3).expectData(2, 33, 8.1d, 11);
    }

    @Test
    public void insert_Negative() {
        assertThrows(IllegalArgumentException.class, () -> Series.ofDouble(2, 33).insert(-1, "abc", 4f));
    }

    @Test
    public void insert_PastEnd() {
        assertThrows(IllegalArgumentException.class, () -> Series.ofDouble(2, 33).insert(3, "abc", 4f));
    }
}
