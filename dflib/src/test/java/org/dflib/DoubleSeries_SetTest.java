package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class DoubleSeries_SetTest {

    @Test
    void set() {
        DoubleSeries s = Series.ofDouble(5.1d, 4.01d);
        assertSame(s, s.set(1, 4.01d));

        new SeriesAsserts(s.set(0, 3.1d)).expectData(3.1d, 4.01d);
    }

    @Test
    void set_Nulls() {
        DoubleSeries s = Series.ofDouble(5.1d, 4.01d);
        new SeriesAsserts(s.set(0, null)).expectData(null, 4.01d);
    }

    @Test
    void setFloat() {
        DoubleSeries s = Series.ofDouble(5.1d, 4.01d);
        assertSame(s, s.setDouble(1, 4.01d));

        new SeriesAsserts(s.setDouble(0, 3.1d)).expectData(3.1d, 4.01d);
    }
}