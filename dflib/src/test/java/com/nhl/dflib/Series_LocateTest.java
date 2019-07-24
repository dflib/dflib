package com.nhl.dflib;

import com.nhl.dflib.unit.BooleanSeriesAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

@RunWith(Parameterized.class)
public class Series_LocateTest extends BaseObjectSeriesTest {

    public Series_LocateTest(SeriesTypes seriesType) {
        super(seriesType);
    }


    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ALL_SERIES_TYPES;
    }

    @Test
    public void testLocate() {
        BooleanSeries evens = createSeries(3, 4, 2).locate(i -> i % 2 == 0);
        new BooleanSeriesAsserts(evens).expectData(false, true, true);
    }
}
