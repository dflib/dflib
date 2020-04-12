package com.nhl.dflib.series;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class ByRowSeries_HeadTest {

    private <T> ByRowSeries<T> createSeries(T... data) {
        return new ByRowSeries<>(DataFrame.newFrame("a", "b").foldByRow(data));
    }

    @Test
    public void test() {
        Series<String> s = createSeries("a", "b", "c").head(2);
        new SeriesAsserts(s).expectData("a", "b");
    }

    @Test
    public void test_Zero() {
        Series<String> s = createSeries("a", "b", "c").head(0);
        new SeriesAsserts(s).expectData();
    }

    @Test
    public void test_OutOfBounds() {
        Series<String> s = createSeries("a", "b", "c", "d").head(5);
        new SeriesAsserts(s).expectData("a", "b", "c", "d");
    }
}
