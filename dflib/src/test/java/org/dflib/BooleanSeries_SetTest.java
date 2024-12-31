package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class BooleanSeries_SetTest {


    @Test
    void setBool() {
        BooleanSeries s = Series.ofBool(true, false);
        assertSame(s, s.set(1, false));

        new SeriesAsserts(s.set(0, false)).expectData(false, false);
    }

    @Test
    void setBool_Nulls() {
        BooleanSeries s = Series.ofBool(true, false);
        new SeriesAsserts(s.set(0, null)).expectData(null, false);
    }
}
