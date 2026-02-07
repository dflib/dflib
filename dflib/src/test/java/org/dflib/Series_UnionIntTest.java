package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;

public class Series_UnionIntTest {

    @Test
    public void none() {
        new SeriesAsserts(Series.unionInt()).expectData();
    }

    @Test
    public void one() {
        IntSeries s = Series.ofInt(1, 2);
        assertSame(s, Series.unionInt(s));
    }

    @Test
    public void repeated() {
        IntSeries s = Series.ofInt(1, 2);
        IntSeries c = Series.unionInt(s, s);
        new SeriesAsserts(c).expectData(1, 2, 1, 2);
    }

    @Test
    public void many() {
        IntSeries s1 = Series.ofInt(1, 2);
        IntSeries s2 = Series.ofInt(-1, -2);
        IntSeries s3 = Series.ofInt(0);

        IntSeries c = Series.unionInt(s1, s2, s3);
        new SeriesAsserts(c).expectData(1, 2, -1, -2, 0);
    }

    @Test
    public void manyIterable() {
        IntSeries s1 = Series.ofInt(1, 2);
        IntSeries s2 = Series.ofInt(-1, -2);
        IntSeries s3 = Series.ofInt(0);

        IntSeries c = Series.unionInt(List.of(s1, s2, s3));
        new SeriesAsserts(c).expectData(1, 2, -1, -2, 0);
    }
}

