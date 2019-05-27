package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

import static org.junit.Assert.*;

public class EnumSeries_ConcatTest {

    @Test
    public void test_None() {
        Series<X> s = Series.forEnums(X.a, X.b);
        assertSame(s, s.concat());
    }

    @Test
    public void test_Self() {
        Series<X> s = Series.forEnums(X.a, X.b);
        Series<X> c = s.concat(s);
        new SeriesAsserts(c).expectData(X.a, X.b, X.a, X.b);
    }

    @Test
    public void test() {
        Series<X> s1 = Series.forEnums(X.c, X.d);
        Series<X> s2 = Series.forEnums(X.a, X.b);
        Series<X> s3 = Series.forEnums(X.f, X.e);

        Series<X> c = s1.concat(s2, s3);
        new SeriesAsserts(c).expectData(X.c, X.d, X.a, X.b, X.f, X.e);
    }

    enum X {a, b, c, d, e, f}
}
