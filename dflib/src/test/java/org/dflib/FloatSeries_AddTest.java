package org.dflib;

import org.dflib.unit.FloatSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class FloatSeries_AddTest {

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

    @Test
    public void add_Series() {

        // using a longer series to test optional vectorization extensions
        FloatSeries s0 = Series.ofFloat(1, 2, 3, 4, 5, 6.56f);
        FloatSeries s = Series.ofFloat(3, 28, 15, -4, 3, 11).add(s0);
        new FloatSeriesAsserts(s).expectData(4.f, 30.f, 18.f, 0.f, 8.f, 17.56f);
    }

    @Test
    public void add_Value() {

        FloatSeries s = Series.ofFloat(3, 28, 15, -4, 3, 11).add(10.5f);
        new FloatSeriesAsserts(s).expectData(13.5f, 38.5f, 25.5f, 6.5f, 13.5f, 21.5f);
    }
}
