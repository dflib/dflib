package org.dflib.series;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class ByRowSeries_TailTest {

    private ByRowSeries createSeries(Object... data) {
        return new ByRowSeries(DataFrame.foldByRow("a", "b").of(data));
    }

    @Test
    public void test() {
        Series<Object> s = createSeries("a", "b", "c", "d").tail(2);
        new SeriesAsserts(s).expectData("c", "d");
    }

    @Test
    public void zero() {
        Series<Object> s = createSeries("a", "b", "c", "d").tail(0);
        new SeriesAsserts(s).expectData();
    }

    @Test
    public void outOfBounds() {
        Series<Object> s = createSeries("a", "b").tail(3);
        new SeriesAsserts(s).expectData("a", "b");
    }

    @Test
    public void negative() {
        Series<Object> s = createSeries("a", "b", "c").tail(-2);
        new SeriesAsserts(s).expectData("a", "b");
    }
}
