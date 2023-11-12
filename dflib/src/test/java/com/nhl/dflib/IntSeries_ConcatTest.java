package com.nhl.dflib;

import com.nhl.dflib.Series;
import com.nhl.dflib.series.IntArraySeries;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// mirrors Series_ConcatTest
public class IntSeries_ConcatTest {

    @Test
    public void none() {
        Series<Integer> s = new IntArraySeries(1, 2);
        assertSame(s, s.concat());
    }

    @Test
    public void self() {
        Series<Integer> s = new IntArraySeries(1, 2);
        Series<Integer> c = s.concat(s);
        new SeriesAsserts(c).expectData(1, 2, 1, 2);
    }

    @Test
    public void test() {
        Series<Integer> s1 = new IntArraySeries(5, 6);
        Series<Integer> s2 = new IntArraySeries(1, 2);
        Series<Integer> s3 = new IntArraySeries(3, 4);

        Series<Integer> c = s1.concat(s2, s3);
        new SeriesAsserts(c).expectData(5, 6, 1, 2, 3, 4);
    }
}
