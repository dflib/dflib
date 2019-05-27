package com.nhl.dflib;

import com.nhl.dflib.unit.BooleanSeriesAsserts;
import org.junit.Test;

public class EnumSeries_ConditionsTest {

    @Test
    public void testEq1() {

        Series<X> s1 = Series.forEnums(X.c, X.a, X.b);
        Series<X> s2 = Series.forEnums(X.c, X.a, X.b);

        BooleanSeries cond = s1.eq(s2);
        new BooleanSeriesAsserts(cond).expectData(true, true, true);
    }

    @Test
    public void testEq2() {

        Series<X> s1 = Series.forEnums(X.c, X.d, X.b);
        Series<X> s2 = Series.forEnums(X.c, X.a, X.b);

        BooleanSeries cond = s1.eq(s2);
        new BooleanSeriesAsserts(cond).expectData(true, false, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEq_SizeMismatch() {

        Series<X> s1 = Series.forEnums(X.c, X.d, X.b);
        Series<X> s2 = Series.forEnums(X.c, X.a);

        s1.eq(s2);
    }

    @Test
    public void testNe1() {

        Series<X> s1 = Series.forEnums(X.c, X.a, X.b);
        Series<X> s2 = Series.forEnums(X.c, X.a, X.b);

        BooleanSeries cond = s1.ne(s2);
        new BooleanSeriesAsserts(cond).expectData(false, false, false);
    }

    @Test
    public void testNe2() {

        Series<X> s1 = Series.forEnums(X.c, X.a, X.b);
        Series<X> s2 = Series.forEnums(X.f, X.b, X.b);

        BooleanSeries cond = s1.ne(s2);
        new BooleanSeriesAsserts(cond).expectData(true, true, false);
    }

    enum X {a, b, c, d, e, f}

}
