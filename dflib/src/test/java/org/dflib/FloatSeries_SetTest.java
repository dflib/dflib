package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class FloatSeries_SetTest {

    @Test
    void set() {
        FloatSeries s = Series.ofFloat(5.1f, 4.01f);
        assertSame(s, s.set(1, 4.01f));

        new SeriesAsserts(s.set(0, 3.1f)).expectData(3.1f, 4.01f);
    }

    @Test
    void set_Nulls() {
        FloatSeries s = Series.ofFloat(5.1f, 4.01f);
        new SeriesAsserts(s.set(0, null)).expectData(null, 4.01f);
    }

    @Test
    void setFloat() {
        FloatSeries s = Series.ofFloat(5.1f, 4.01f);
        assertSame(s, s.setFloat(1, 4.01f));

        new SeriesAsserts(s.setFloat(0, 3.1f)).expectData(3.1f, 4.01f);
    }
}