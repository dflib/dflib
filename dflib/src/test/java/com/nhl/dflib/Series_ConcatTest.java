package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertSame;

public class Series_ConcatTest {

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testConcat_None(SeriesType type) {
        Series<String> s = type.createSeries("a", "b");
        assertSame(s, s.concat());
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testConcat_Self(SeriesType type) {
        Series<String> s = type.createSeries("a", "b");
        Series<String> c = s.concat(s);
        new SeriesAsserts(c).expectData("a", "b", "a", "b");
    }

    @ParameterizedTest
    @EnumSource(SeriesType.class)
    public void testConcat(SeriesType type) {
        Series<String> s1 = type.createSeries("m", "n");
        Series<String> s2 = type.createSeries("a", "b");
        Series<String> s3 = type.createSeries("d", "c");

        Series<String> c = s1.concat(s2, s3);
        new SeriesAsserts(c).expectData("m", "n", "a", "b", "d", "c");
    }
}

