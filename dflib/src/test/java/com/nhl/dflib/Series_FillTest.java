package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

@RunWith(Parameterized.class)
public class Series_FillTest extends BaseSeriesTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ALL_SERIES_TYPES;
    }

    public Series_FillTest(SeriesTypes seriesType) {
        super(seriesType);
    }

    @Test
    public void testFillNulls() {
        Series<Integer> s = createSeries(1, null, 5, 8, null).fillNulls(-1);
        new SeriesAsserts(s).expectData(1, -1, 5, 8, -1);
    }

    @Test
    public void testFillNullsBackwards() {
        Series<Integer> s = createSeries(null, 1, null, 5, 8, null).fillNullsBackwards();
        new SeriesAsserts(s).expectData(1, 1, 5, 5, 8, null);
    }

    @Test
    public void testFillNullsForward() {
        Series<Integer> s = createSeries(null, 1, null, 5, 8, null).fillNullsForward();
        new SeriesAsserts(s).expectData(null, 1, 1, 5, 8, 8);
    }
}
