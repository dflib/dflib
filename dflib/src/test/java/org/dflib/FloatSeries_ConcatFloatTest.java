package org.dflib;

import org.dflib.series.FloatArraySeries;
import org.dflib.unit.FloatSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class FloatSeries_ConcatFloatTest {

    @Test
    public void none() {
        FloatSeries s = new FloatArraySeries(1, 2);
        assertSame(s, s.concatFloat());
    }

    @Test
    public void self() {
        FloatSeries s = new FloatArraySeries(1, 2);
        FloatSeries c = s.concatFloat(s);
        new FloatSeriesAsserts(c).expectData(1, 2, 1, 2);
    }

    @Test
    public void test() {
        FloatSeries s1 = new FloatArraySeries(34, 23);
        FloatSeries s2 = new FloatArraySeries(1, 2);
        FloatSeries s3 = new FloatArraySeries(-1, -6);

        FloatSeries c = s1.concatFloat(s2, s3);
        new FloatSeriesAsserts(c).expectData(34, 23, 1, 2, -1, -6);
    }
}
