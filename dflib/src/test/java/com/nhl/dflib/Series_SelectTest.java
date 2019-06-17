package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

@RunWith(Parameterized.class)
public class Series_SelectTest extends BaseObjectSeriesTest {

    public Series_SelectTest(SeriesTypes seriesType) {
        super(seriesType);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ALL_SERIES_TYPES;
    }

    @Test
    public void testPositional() {
        Series<Integer> s = createSeries(3, 4, 2).select(2, 1);
        new SeriesAsserts(s).expectData(2, 4);
    }

    @Test
    public void testPositional_Empty() {
        Series<Integer> s = createSeries(3, 4, 2).select();
        new SeriesAsserts(s).expectData();
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testPositional_OutOfBounds() {
        createSeries(3, 4, 2).select(0, 3).materialize();
    }

    @Test
    public void testPositionalNulls() {
        Series<Integer> s = createSeries(3, 4, 2).select(2, 1, -1);
        new SeriesAsserts(s).expectData(2, 4, null);
    }
}
