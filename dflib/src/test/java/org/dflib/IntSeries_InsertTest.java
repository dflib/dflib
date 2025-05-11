package org.dflib;

import org.dflib.unit.IntSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntSeries_InsertTest {

    @Test
    public void insert_empty() {
        Series s0 = Series.ofInt(2, 33);
        assertSame(s0, s0.insert(1));
    }

    @Test
    public void insert() {
        Series<?> s1 = Series.ofInt(2, 33).insert(0, 8, "abc");
        new SeriesAsserts(s1).expectData(8, "abc", 2, 33);

        Series<?> s2 = Series.ofInt(2, 33).insert(1, 8, "abc");
        new SeriesAsserts(s2).expectData(2, 8, "abc", 33);

        Series<?> s3 = Series.ofInt(2, 33).insert(2, 8, "abc");
        new SeriesAsserts(s3).expectData(2, 33, 8, "abc");
    }

    @Test
    public void insert_IntObjects() {
        Series<?> s1 = Series.ofInt(2, 33).insert(1, 8, 11);
        assertTrue(s1 instanceof IntSeries);
        new IntSeriesAsserts((IntSeries) s1).expectData(2, 8, 11, 33);
    }

    @Test
    public void insertInt() {
        IntSeries s1 = Series.ofInt(2, 33).insertInt(0, 8, 11);
        new IntSeriesAsserts(s1).expectData(8, 11, 2, 33);

        IntSeries s2 = Series.ofInt(2, 33).insertInt(1, 8, 11);
        new IntSeriesAsserts(s2).expectData(2, 8, 11, 33);

        IntSeries s3 = Series.ofInt(2, 33).insertInt(2, 8, 11);
        new IntSeriesAsserts(s3).expectData(2, 33, 8, 11);
    }

    @Test
    public void insert_Negative() {
        assertThrows(IllegalArgumentException.class, () -> Series.ofInt(2, 33).insert(-1, "abc", 4));
    }

    @Test
    public void insert_PastEnd() {
        assertThrows(IllegalArgumentException.class, () -> Series.ofInt(2, 33).insert(3, "abc", 4));
    }
}
