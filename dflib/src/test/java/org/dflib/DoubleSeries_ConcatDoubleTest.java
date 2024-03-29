package org.dflib;

import org.dflib.series.DoubleArraySeries;
import org.dflib.unit.DoubleSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DoubleSeries_ConcatDoubleTest {

    @Test
    public void none() {
        DoubleSeries s = new DoubleArraySeries(1, 2);
        assertSame(s, s.concatDouble());
    }

    @Test
    public void self() {
        DoubleSeries s = new DoubleArraySeries(1, 2);
        DoubleSeries c = s.concatDouble(s);
        new DoubleSeriesAsserts(c).expectData(1, 2, 1, 2);
    }

    @Test
    public void test() {
        DoubleSeries s1 = new DoubleArraySeries(34, 23);
        DoubleSeries s2 = new DoubleArraySeries(1, 2);
        DoubleSeries s3 = new DoubleArraySeries(-1, -6);

        DoubleSeries c = s1.concatDouble(s2, s3);
        new DoubleSeriesAsserts(c).expectData(34, 23, 1, 2, -1, -6);
    }
}
