package com.nhl.dflib.series;

import com.nhl.dflib.Series;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

public class EmptySeriesTest {

    @Test
    public void select() {
        Series<Object> s = new EmptySeries<>().select(Series.ofBool(true, false));
        new SeriesAsserts(s).expectData();
    }

    @Test
    public void sort() {
        Series<Object> s = new EmptySeries<>().sort(Comparator.comparing(Object::toString));
        new SeriesAsserts(s).expectData();
    }
}
