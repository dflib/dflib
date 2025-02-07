package org.dflib;

import org.dflib.unit.BoolSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BooleanSeries_InsertTest {

    @Test
    public void insert_empty() {
        Series s0 = Series.ofBool(true, false);
        assertSame(s0, s0.insert(1));
    }

    @Test
    public void insert() {
        Series<?> s1 = Series.ofBool(true, false).insert(0, true, "abc");
        new SeriesAsserts(s1).expectData(true, "abc", true, false);

        Series<?> s2 = Series.ofBool(true, false).insert(1, true, "abc");
        new SeriesAsserts(s2).expectData(true, true, "abc", false);

        Series<?> s3 = Series.ofBool(true, false).insert(2, true, "abc");
        new SeriesAsserts(s3).expectData(true, false, true, "abc");
    }

    @Test
    public void insert_BoolObjects() {
        Series<?> s1 = Series.ofBool(true, false).insert(1, false, true);
        assertTrue(s1 instanceof BooleanSeries);
        new BoolSeriesAsserts((BooleanSeries) s1).expectData(true, false, true, false);
    }

    @Test
    public void insertBool() {
        BooleanSeries s1 = Series.ofBool(true, false).insertBool(0, true, true);
        new BoolSeriesAsserts(s1).expectData(true, true, true, false);

        BooleanSeries s2 = Series.ofBool(true, false).insertBool(1, true, true);
        new BoolSeriesAsserts(s2).expectData(true, true, true, false);

        BooleanSeries s3 = Series.ofBool(true, false).insertBool(2, true, true);
        new BoolSeriesAsserts(s3).expectData(true, false, true, true);
    }

    @Test
    public void insert_Negative() {
        assertThrows(IllegalArgumentException.class, () -> Series.ofBool(true, false).insert(-1, "abc", 4));
    }

    @Test
    public void insert_PastEnd() {
        assertThrows(IllegalArgumentException.class, () -> Series.ofBool(true, false).insert(3, "abc", 4));
    }
}
