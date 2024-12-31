package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class IntSeries_SetTest {

    @Test
    void set() {
        IntSeries s = Series.ofInt(5, 4);
        assertSame(s, s.set(1, 4));

        new SeriesAsserts(s.set(0, 3)).expectData(3, 4);
    }

    @Test
    void set_Nulls() {
        IntSeries s = Series.ofInt(5, 4);
        new SeriesAsserts(s.set(0, null)).expectData(null, 4);
    }

    @Test
    void setInt() {
        IntSeries s = Series.ofInt(5, 4);
        assertSame(s, s.setInt(1, 4));

        new SeriesAsserts(s.setInt(0, 3)).expectData(3, 4);
    }
}