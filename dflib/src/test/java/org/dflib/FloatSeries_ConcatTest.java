package org.dflib;

import org.dflib.series.ArraySeries;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Deprecated
public class FloatSeries_ConcatTest {

    @Test
    public void none() {
        FloatSeries s = Series.ofFloat(1.1f, 2.2f);
        assertSame(s, s.concat());
    }

    @Test
    public void self() {
        FloatSeries s = Series.ofFloat(1.1f, 2.2f);
        Series<Float> c = s.concat(s);
        new SeriesAsserts(c).expectData(1.1f, 2.2f, 1.1f, 2.2f);
        assertTrue(c instanceof FloatSeries);
    }

    @Test
    public void intSeries() {
        FloatSeries s1 = Series.ofFloat(5.5f, 6.6f);
        FloatSeries s2 = Series.ofFloat(1.1f, 2.2f);
        FloatSeries s3 = Series.ofFloat(3.3f, 4.4f);

        Series<Float> c = s1.concat(s2, s3);
        new SeriesAsserts(c).expectData(5.5f, 6.6f, 1.1f, 2.2f, 3.3f, 4.4f);
        assertTrue(c instanceof FloatSeries);
    }

    @Test
    public void primitveAndNonPrimitiveSeries() {
        FloatSeries s1 = Series.ofFloat(5.5f, 6.6f);
        Series<Float> s2 = new ArraySeries<>(1.1f, 2.2f, null);
        FloatSeries s3 = Series.ofFloat(3.3f, 4.4f);

        Series<Float> c = s1.concat(s2, s3);
        new SeriesAsserts(c).expectData(5.5f, 6.6f, 1.1f, 2.2f, null, 3.3f, 4.4f);
        assertFalse(c instanceof FloatSeries);
    }
}
