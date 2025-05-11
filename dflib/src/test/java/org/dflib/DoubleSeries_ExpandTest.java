package org.dflib;

import org.dflib.unit.DoubleSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class DoubleSeries_ExpandTest {

    @Test
    public void expand() {
        Series<?> s = Series.ofDouble(3., 28.).expand("abc");
        new SeriesAsserts(s).expectData(3., 28., "abc");
    }

    @Test
    public void expandDouble() {
        DoubleSeries s = Series.ofDouble(3., 28.).expandDouble(5.3);
        new DoubleSeriesAsserts(s).expectData(3., 28., 5.3);
    }
}
