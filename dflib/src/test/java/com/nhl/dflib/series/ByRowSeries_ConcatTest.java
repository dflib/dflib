package com.nhl.dflib.series;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ByRowSeries_ConcatTest {

    private <T> ByRowSeries<T> createSeries(T... data) {
        return new ByRowSeries<>(DataFrame.newFrame("a", "b").foldByRow(data));
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
