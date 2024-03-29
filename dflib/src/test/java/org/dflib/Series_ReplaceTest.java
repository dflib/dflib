package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class Series_ReplaceTest {

    @Test
    public void replace_positions() {

        Series<String> s1 = Series.of("a", "b", "n", "c").replace(
                Series.ofInt(1, 3),
                Series.of("B", "C"));

        new SeriesAsserts(s1).expectData("a", "B", "n", "C");
    }

    @Test
    public void replace() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<String> s1 = Series.of("a", "b", "n", "c").replace(cond, "X");
        new SeriesAsserts(s1).expectData("X", "X", "n", "c");
    }

    @Test
    public void replace_Null() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<String> s1 = Series.of("a", "b", "n", "c").replace(cond, null);
        new SeriesAsserts(s1).expectData(null, null, "n", "c");
    }

    @Test
    public void replace_SmallerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false);

        Series<String> s1 = Series.of("a", "b", "n", "c").replace(cond, "X");
        new SeriesAsserts(s1).expectData("X", "X", "n", "c");
    }

    @Test
    public void replaceExcept() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<String> s1 = Series.of("a", "b", "n", "c").replaceExcept(cond, "X");
        new SeriesAsserts(s1).expectData("a", "b", "X", "X");
    }

    @Test
    public void replaceExcept_Null() {
        BooleanSeries cond = Series.ofBool(true, true, false, false);

        Series<String> s1 = Series.of("a", "b", "n", "c").replaceExcept(cond, null);
        new SeriesAsserts(s1).expectData("a", "b", null, null);
    }

    @Test
    public void replaceExcept_LargerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false, false, false);

        Series<String> s1 = Series.of("a", "b", "n", "c").replaceExcept(cond, "X");
        new SeriesAsserts(s1).expectData("a", "b", "X", "X");
    }

    @Test
    public void replaceExcept_SmallerCondition() {
        BooleanSeries cond = Series.ofBool(true, true, false);

        Series<String> s1 = Series.of("a", "b", "n", "c").replaceExcept(cond, "X");
        new SeriesAsserts(s1).expectData("a", "b", "X", "X");
    }
}
