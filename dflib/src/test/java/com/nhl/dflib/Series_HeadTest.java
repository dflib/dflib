package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

@RunWith(Parameterized.class)
public class Series_HeadTest extends BaseObjectSeriesTest {

    public Series_HeadTest(SeriesTypes seriesType) {
        super(seriesType);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ALL_SERIES_TYPES;
    }

    @Test
    public void test() {
        Series<String> s = createSeries("a", "b", "c").head(2);
        new SeriesAsserts(s).expectData("a", "b");
    }

    @Test
    public void test_Zero() {
        Series<String> s = createSeries("a", "b", "c").head(0);
        new SeriesAsserts(s).expectData();
    }

    @Test
    public void test_OutOfBounds() {
        Series<String> s = createSeries("a", "b", "c").head(4);
        new SeriesAsserts(s).expectData("a", "b", "c");
    }
}
