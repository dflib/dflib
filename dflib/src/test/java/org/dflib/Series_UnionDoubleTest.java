package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

public class Series_UnionDoubleTest {

    @Test
    public void none() {
        new SeriesAsserts(Series.unionDouble()).expectData();
    }

    @Test
    public void one() {
        DoubleSeries s = Series.ofDouble(1.1, 2.2);
        assertSame(s, Series.unionDouble(s));
    }

    @Test
    public void repeated() {
        DoubleSeries s = Series.ofDouble(1.5, 2.7);
        DoubleSeries c = Series.unionDouble(s, s);
        new SeriesAsserts(c).expectData(1.5, 2.7, 1.5, 2.7);
    }

    @Test
    public void many() {
        DoubleSeries s1 = Series.ofDouble(1.1, 2.2);
        DoubleSeries s2 = Series.ofDouble(-1.3, -2.4);
        DoubleSeries s3 = Series.ofDouble(0.5);

        DoubleSeries c = Series.unionDouble(s1, s2, s3);
        new SeriesAsserts(c).expectData(1.1, 2.2, -1.3, -2.4, 0.5);
    }

    @Test
    public void manyIterable() {
        DoubleSeries s1 = Series.ofDouble(1.1, 2.2);
        DoubleSeries s2 = Series.ofDouble(-1.3, -2.4);
        DoubleSeries s3 = Series.ofDouble(0.5);

        DoubleSeries c = Series.unionDouble(List.of(s1, s2, s3));
        new SeriesAsserts(c).expectData(1.1, 2.2, -1.3, -2.4, 0.5);
    }
}
