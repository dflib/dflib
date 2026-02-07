package org.dflib;

import org.dflib.unit.DoubleSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

@Deprecated
public class DoubleSeries_ConcatDoubleTest {

    @Test
    public void none() {
        DoubleSeries s = Series.ofDouble(1, 2);
        assertSame(s, s.concatDouble());
    }

    @Test
    public void self() {
        DoubleSeries s = Series.ofDouble(1, 2);
        DoubleSeries c = s.concatDouble(s);
        new DoubleSeriesAsserts(c).expectData(1, 2, 1, 2);
    }

    @Test
    public void test() {
        DoubleSeries s1 = Series.ofDouble(34, 23);
        DoubleSeries s2 = Series.ofDouble(1, 2);
        DoubleSeries s3 = Series.ofDouble(-1, -6);

        DoubleSeries c = s1.concatDouble(s2, s3);
        new DoubleSeriesAsserts(c).expectData(34, 23, 1, 2, -1, -6);
    }
}
