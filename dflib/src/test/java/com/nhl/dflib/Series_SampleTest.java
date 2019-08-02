package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.Random;

@RunWith(Parameterized.class)
public class Series_SampleTest extends BaseObjectSeriesTest {

    public Series_SampleTest(SeriesTypes seriesType) {
        super(seriesType);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ALL_SERIES_TYPES;
    }

    @Test
    public void testSample() {
        // using fixed seed to get reproducible result
        Series<String> sample = createSeries("a", "b", "c", "d", "e", "f", "g").sample(4, new Random(5));
        new SeriesAsserts(sample).expectData("d", "b", "a", "g");
    }
}
