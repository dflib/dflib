package com.nhl.dflib;

import com.nhl.dflib.unit.BooleanSeriesAsserts;
import org.junit.Test;

public class IntSeries_ConditionsTest {

    @Test
    public void testEq1() {

        Series<Integer> s1 = IntSeries.forInts(3, 1, 2);
        Series<Integer> s2 = IntSeries.forInts(3, 1, 2);

        BooleanSeries cond = s1.eq(s2);
        new BooleanSeriesAsserts(cond).expectData(true, true, true);
    }

    @Test
    public void testEq2() {

        Series<Integer> s1 = IntSeries.forInts(3, 4, 2);
        Series<Integer> s2 = IntSeries.forInts(3, 1, 2);

        BooleanSeries cond = s1.eq(s2);
        new BooleanSeriesAsserts(cond).expectData(true, false, true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEq_SizeMismatch() {

        Series<Integer> s1 = IntSeries.forInts(3, 4, 2);
        Series<Integer> s2 = IntSeries.forInts(3, 1);

        s1.eq(s2);
    }

    @Test
    public void testNe1() {

        Series<Integer> s1 = IntSeries.forInts(3, 1, 2);
        Series<Integer> s2 = IntSeries.forInts(3, 1, 2);

        BooleanSeries cond = s1.ne(s2);
        new BooleanSeriesAsserts(cond).expectData(false, false, false);
    }

    @Test
    public void testNe2() {

        Series<Integer> s1 = IntSeries.forInts(3, 0, 2);
        Series<Integer> s2 = IntSeries.forInts(-3, 1, 2);

        BooleanSeries cond = s1.ne(s2);
        new BooleanSeriesAsserts(cond).expectData(true, true, false);
    }

}
