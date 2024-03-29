package org.dflib;

import org.dflib.unit.IntSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSeries_AddTest {

    @Test
    public void expand() {
        Series<?> s = Series.ofInt(3, 28).expand("abc");
        new SeriesAsserts(s).expectData(3, 28, "abc");
    }

    @Test
    public void expandInt() {
        IntSeries s = Series.ofInt(3, 28).expandInt(5);
        new IntSeriesAsserts(s).expectData(3, 28, 5);
    }

    @Test
    public void add_Series() {
        IntSeries s0 = Series.ofInt(1, 2, 3, 4, 5, 6);
        IntSeries s = Series.ofInt(3, 28, 15, -4, 3, 11).add(s0);
        new IntSeriesAsserts(s).expectData(4, 30, 18, 0, 8, 17);
    }
}
