package com.nhl.dflib.series;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class ByRowSeries_HeadTest {

    private <T> ByRowSeries createSeries(Object... data) {
        return new ByRowSeries(DataFrame.newFrame("a", "b").foldByRow(data));
    }

    @Test
    public void test() {
        Series<Object> s = createSeries("a", "b", "c").head(2);
        new SeriesAsserts(s).expectData("a", "b");
    }

    @Test
    public void zero() {
        Series<Object> s = createSeries("a", "b", "c").head(0);
        new SeriesAsserts(s).expectData();
    }

    @Test
    public void outOfBounds() {
        Series<Object> s = createSeries("a", "b", "c", "d").head(5);
        new SeriesAsserts(s).expectData("a", "b", "c", "d");
    }

    @Test
    public void negative() {
        Series<Object> s = createSeries("a", "b", "c").head(-2);
        new SeriesAsserts(s).expectData("c", null);
    }
}
