package com.nhl.dflib;

import com.nhl.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class Series_ConditionsTest {

    @Test
    public void testEq1() {

        Series<String> s1 = Series.of("a", "b", "n", "c");
        Series<String> s2 = Series.of("a", "b", "n", "c");

        BooleanSeries cond = s1.eq(s2);
        new BoolSeriesAsserts(cond).expectData(true, true, true, true);
    }

    @Test
    public void testEq2() {

        Series<String> s1 = Series.of("a", "b", "n", "c");
        Series<String> s2 = Series.of("a ", "b", "N", "c");

        BooleanSeries cond = s1.eq(s2);
        new BoolSeriesAsserts(cond).expectData(false, true, false, true);
    }

    @Test
    public void testEq_SizeMismatch() {
        Series<String> s1 = Series.of("a", "b", "n", "c");
        Series<String> s2 = Series.of("a", "b", "n");

        assertThrows(IllegalArgumentException.class, () -> s1.eq(s2));
    }

    @Test
    public void testNe1() {

        Series<String> s1 = Series.of("a", "b", "n", "c");
        Series<String> s2 = Series.of("a", "b", "n", "c");

        BooleanSeries cond = s1.ne(s2);
        new BoolSeriesAsserts(cond).expectData(false, false, false, false);
    }

    @Test
    public void testNe2() {

        Series<String> s1 = Series.of("a", "b", "n", "c");
        Series<String> s2 = Series.of("a ", "b", "N", "c");

        BooleanSeries cond = s1.ne(s2);
        new BoolSeriesAsserts(cond).expectData(true, false, true, false);
    }

}
