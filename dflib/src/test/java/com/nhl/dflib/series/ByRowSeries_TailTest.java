package com.nhl.dflib.series;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;

public class ByRowSeries_TailTest {

    private <T> ByRowSeries<T> createSeries(T... data) {
        return new ByRowSeries<>(DataFrame.forSequenceFoldByRow(Index.forLabels("a", "b"), data));
    }

    @Test
    public void test() {
        Series<String> s = createSeries("a", "b", "c", "d").tail(2);
        new SeriesAsserts(s).expectData("c", "d");
    }

    @Test
    public void test_Zero() {
        Series<String> s = createSeries("a", "b", "c", "d").tail(0);
        new SeriesAsserts(s).expectData();
    }

    @Test
    public void test_OutOfBounds() {
        Series<String> s = createSeries("a", "b").tail(3);
        new SeriesAsserts(s).expectData("a", "b");
    }
}
