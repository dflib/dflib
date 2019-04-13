package com.nhl.dflib.series;

import com.nhl.dflib.Series;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

import static org.junit.Assert.*;

// mirrors Series_ConcatTest
public class IntSeries_ConcatTest {

    private IntSeries createSeries(int... vals) {
        return new IntSeries(vals);
    }

    @Test
    public void testConcat_None() {
        Series<Integer> s = createSeries(1, 2);
        assertSame(s, s.concat());
    }

    @Test
    public void testConcat_Self() {
        Series<Integer> s = createSeries(1, 2);
        Series<Integer> c = s.concat(s);
        new SeriesAsserts(c).expectData(1, 2, 1, 2);
    }

    @Test
    public void testConcat() {
        Series<Integer> s1 = createSeries(5, 6);
        Series<Integer> s2 = createSeries(1, 2);
        Series<Integer> s3 = createSeries(3, 4);

        Series<Integer> c = s1.concat(s2, s3);
        new SeriesAsserts(c).expectData(5, 6, 1, 2, 3, 4);
    }
}
