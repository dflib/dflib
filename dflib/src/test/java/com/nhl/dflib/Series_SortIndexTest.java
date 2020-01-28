package com.nhl.dflib;

import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.Comparator;

@RunWith(Parameterized.class)
public class Series_SortIndexTest extends BaseObjectSeriesTest {

    public Series_SortIndexTest(SeriesTypes seriesType) {
        super(seriesType);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ALL_SERIES_TYPES;
    }

    @Test
    public void test() {
        IntSeries s = createSeries("x", "b", "c", "a").sortIndex(Comparator.naturalOrder());
        new IntSeriesAsserts(s).expectData(3, 1, 2, 0);
    }
}