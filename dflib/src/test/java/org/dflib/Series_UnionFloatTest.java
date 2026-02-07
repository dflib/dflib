package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

public class Series_UnionFloatTest {

    @Test
    public void none() {
        new SeriesAsserts(Series.unionFloat()).expectData();
    }

    @Test
    public void one() {
        FloatSeries s = Series.ofFloat(1.1f, 2.2f);
        assertSame(s, Series.unionFloat(s));
    }

    @Test
    public void repeated() {
        FloatSeries s = Series.ofFloat(1.5f, 2.7f);
        FloatSeries c = Series.unionFloat(s, s);
        new SeriesAsserts(c).expectData(1.5f, 2.7f, 1.5f, 2.7f);
    }

    @Test
    public void many() {
        FloatSeries s1 = Series.ofFloat(1.1f, 2.2f);
        FloatSeries s2 = Series.ofFloat(-1.3f, -2.4f);
        FloatSeries s3 = Series.ofFloat(0.5f);

        FloatSeries c = Series.unionFloat(s1, s2, s3);
        new SeriesAsserts(c).expectData(1.1f, 2.2f, -1.3f, -2.4f, 0.5f);
    }

    @Test
    public void manyIterable() {
        FloatSeries s1 = Series.ofFloat(1.1f, 2.2f);
        FloatSeries s2 = Series.ofFloat(-1.3f, -2.4f);
        FloatSeries s3 = Series.ofFloat(0.5f);

        FloatSeries c = Series.unionFloat(List.of(s1, s2, s3));
        new SeriesAsserts(c).expectData(1.1f, 2.2f, -1.3f, -2.4f, 0.5f);
    }
}
