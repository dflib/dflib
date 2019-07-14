package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.Comparator;

@RunWith(Parameterized.class)
public class Series_SortTest extends BaseObjectSeriesTest{

    public Series_SortTest(SeriesTypes seriesType) {
        super(seriesType);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ALL_SERIES_TYPES;
    }

    @Test
    public void test() {
        Series<String> s = createSeries("x", "b", "c", "a").sort(Comparator.naturalOrder());
        new SeriesAsserts(s).expectData("a", "b", "c", "x");
    }
}
