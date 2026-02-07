package org.dflib;

import org.dflib.series.ArraySeries;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Deprecated
public class DoubleSeries_ConcatTest {

    @Test
    public void none() {
        DoubleSeries s = Series.ofDouble(1.1, 2.2);
        assertSame(s, s.concat());
    }

    @Test
    public void self() {
        DoubleSeries s = Series.ofDouble(1.1, 2.2);
        Series<Double> c = s.concat(s);
        new SeriesAsserts(c).expectData(1.1, 2.2, 1.1, 2.2);
        assertTrue(c instanceof DoubleSeries);
    }

    @Test
    public void intSeries() {
        DoubleSeries s1 = Series.ofDouble(5.5, 6.6);
        DoubleSeries s2 = Series.ofDouble(1.1, 2.2);
        DoubleSeries s3 = Series.ofDouble(3.3, 4.4);

        Series<Double> c = s1.concat(s2, s3);
        new SeriesAsserts(c).expectData(5.5, 6.6, 1.1, 2.2, 3.3, 4.4);
        assertTrue(c instanceof DoubleSeries);
    }

    @Test
    public void primitveAndNonPrimitiveSeries() {
        DoubleSeries s1 = Series.ofDouble(5.5, 6.6);
        Series<Double> s2 = new ArraySeries<>(1.1, 2.2, null);
        DoubleSeries s3 = Series.ofDouble(3.3, 4.4);

        Series<Double> c = s1.concat(s2, s3);
        new SeriesAsserts(c).expectData(5.5, 6.6, 1.1, 2.2, null, 3.3, 4.4);
        assertFalse(c instanceof DoubleSeries);
    }
}
