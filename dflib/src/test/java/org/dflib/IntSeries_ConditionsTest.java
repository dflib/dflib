package org.dflib;

import org.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class IntSeries_ConditionsTest {

    @Test
    public void eq1() {

        Series<Integer> s1 = Series.ofInt(3, 1, 2);
        Series<Integer> s2 = Series.ofInt(3, 1, 2);

        BooleanSeries cond = s1.eq(s2);
        new BoolSeriesAsserts(cond).expectData(true, true, true);
    }

    @Test
    public void eq2() {

        Series<Integer> s1 = Series.ofInt(3, 4, 2);
        Series<Integer> s2 = Series.ofInt(3, 1, 2);

        BooleanSeries cond = s1.eq(s2);
        new BoolSeriesAsserts(cond).expectData(true, false, true);
    }

    @Test
    public void eq_SizeMismatch() {

        Series<Integer> s1 = Series.ofInt(3, 4, 2);
        Series<Integer> s2 = Series.ofInt(3, 1);

        assertThrows(IllegalArgumentException.class, () -> s1.eq(s2));
    }

    @Test
    public void ne1() {

        Series<Integer> s1 = Series.ofInt(3, 1, 2);
        Series<Integer> s2 = Series.ofInt(3, 1, 2);

        BooleanSeries cond = s1.ne(s2);
        new BoolSeriesAsserts(cond).expectData(false, false, false);
    }

    @Test
    public void ne2() {

        Series<Integer> s1 = Series.ofInt(3, 0, 2);
        Series<Integer> s2 = Series.ofInt(-3, 1, 2);

        BooleanSeries cond = s1.ne(s2);
        new BoolSeriesAsserts(cond).expectData(true, true, false);
    }

}
