package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

@RunWith(Parameterized.class)
public class Series_TailTest extends BaseObjectSeriesTest {

    public Series_TailTest(SeriesTypes seriesType) {
        super(seriesType);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ALL_SERIES_TYPES;
    }

    @Test
    public void test() {
        Series<String> s = createSeries("a", "b", "c").tail(2);
        new SeriesAsserts(s).expectData("b", "c");
    }

    @Test
    public void test_Zero() {
        Series<String> s = createSeries("a", "b", "c").tail(0);
        new SeriesAsserts(s).expectData();
    }

    @Test
    public void test_OutOfBounds() {
        Series<String> s = createSeries("a", "b", "c").tail(4);
        new SeriesAsserts(s).expectData("a", "b", "c");
    }
}
