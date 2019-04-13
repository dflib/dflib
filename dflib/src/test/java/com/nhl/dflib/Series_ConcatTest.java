package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class Series_ConcatTest extends BaseObjectSeriesTest {

    public Series_ConcatTest(SeriesTypes seriesType) {
        super(seriesType);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ALL_SERIES_TYPES;
    }

    @Test
    public void testConcat_None() {
        Series<String> s = createSeries("a", "b");
        assertSame(s, s.concat());
    }

    @Test
    public void testConcat_Self() {
        Series<String> s = createSeries("a", "b");
        Series<String> c = s.concat(s);
        new SeriesAsserts(c).expectData("a", "b", "a", "b");
    }

    @Test
    public void testConcat() {
        Series<String> s1 = createSeries("m", "n");
        Series<String> s2 = createSeries("a", "b");
        Series<String> s3 = createSeries("d", "c");

        Series<String> c = s1.concat(s2, s3);
        new SeriesAsserts(c).expectData("m", "n", "a", "b", "d", "c");
    }
}

