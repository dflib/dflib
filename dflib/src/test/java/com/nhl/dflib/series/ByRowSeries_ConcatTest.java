package com.nhl.dflib.series;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ByRowSeries_ConcatTest {

    private <T> ByRowSeries createSeries(Object... data) {
        return new ByRowSeries(DataFrame.newFrame("a", "b").foldByRow(data));
    }

    @Test
    public void concat_None() {
        Series<Object> s = createSeries("a", "b");
        assertSame(s, s.concat());
    }

    @Test
    public void concat_Self() {
        Series<Object> s = createSeries("a", "b");
        Series<Object> c = s.concat(s);
        new SeriesAsserts(c).expectData("a", "b", "a", "b");
    }

    @Test
    public void concat() {
        Series<Object> s1 = createSeries("m", "n");
        Series<Object> s2 = createSeries("a", "b");
        Series<Object> s3 = createSeries("d", "c");

        Series<Object> c = s1.concat(s2, s3);
        new SeriesAsserts(c).expectData("m", "n", "a", "b", "d", "c");
    }
}
