package org.dflib;

import org.dflib.unit.IntSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSeries_ExpandTest {

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
}
