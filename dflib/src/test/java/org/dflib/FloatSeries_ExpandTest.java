package org.dflib;

import org.dflib.unit.FloatSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class FloatSeries_ExpandTest {

    @Test
    public void expand() {
        Series<?> s = Series.ofFloat(3.f, 28.f).expand("abc");
        new SeriesAsserts(s).expectData(3.f, 28.f, "abc");
    }

    @Test
    public void expandFloat() {
        FloatSeries s = Series.ofFloat(3.f, 28.f).expandFloat(5.3f);
        new FloatSeriesAsserts(s).expectData(3.f, 28.f, 5.3f);
    }

}
