package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Series_InsertTest {

    @Test
    public void insert_empty() {
        Series s0 = Series.of(3, "A");
        assertSame(s0, s0.insert(1));
    }

    @Test
    public void insert() {
        Series<?> s1 = Series.of(3, "A").insert(0, "abc", 4);
        new SeriesAsserts(s1).expectData("abc", 4, 3, "A");

        Series<?> s2 = Series.of(3, "A").insert(1, "abc", 4);
        new SeriesAsserts(s2).expectData(3, "abc", 4, "A");

        Series<?> s3 = Series.of(3, "A").insert(2, "abc", 4);
        new SeriesAsserts(s3).expectData(3, "A", "abc", 4);
    }

    @Test
    public void insert_Negative() {
        assertThrows(IllegalArgumentException.class, () -> Series.of(3, "A").insert(-1, "abc", 4));
    }

    @Test
    public void insert_PastEnd() {
        assertThrows(IllegalArgumentException.class, () -> Series.of(3, "A").insert(3, "abc", 4));
    }
}
