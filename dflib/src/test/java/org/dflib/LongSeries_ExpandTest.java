package org.dflib;

import org.dflib.unit.LongSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class LongSeries_ExpandTest {

    @Test
    public void expand() {
        Series<?> s = Series.ofLong(3, 28).expand("abc");
        new SeriesAsserts(s).expectData(3L, 28L, "abc");
    }

    @Test
    public void expandLong() {
        LongSeries s = Series.ofLong(3, 28).expandLong(5L);
        new LongSeriesAsserts(s).expectData(3L, 28L, 5L);
    }
}
