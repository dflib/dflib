package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

@RunWith(Parameterized.class)
public class Series_UniqueTest extends BaseObjectSeriesTest {

    Object o1 = new Object();
    Object o2 = "__";
    Object o3 = new Integer(9);
    Object o4 = new Integer(9);

    public Series_UniqueTest(SeriesTypes seriesType) {
        super(seriesType);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ALL_SERIES_TYPES;
    }

    @Test
    public void test() {
        Series<Object> s1 = createSeries(o4, o1, o2, o3, o1).unique();
        new SeriesAsserts(s1).expectData(o4, o1, o2);
    }

    @Test
    public void testAlreadyUnique() {
        Series<Object> s1 = createSeries(o4, o1, o2).unique();
        new SeriesAsserts(s1).expectData(o4, o1, o2);
    }

    @Test
    public void testSmall() {
        Series<Object> s1 = createSeries(o4).unique();
        new SeriesAsserts(s1).expectData(o4);
    }
}
