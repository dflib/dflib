package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

@RunWith(Parameterized.class)
public class Series_FilterTest extends BaseObjectSeriesTest {

    public Series_FilterTest(SeriesTypes seriesType) {
        super(seriesType);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ALL_SERIES_TYPES;
    }

    @Test
    public void testFilter_BooleanCondition() {
        BooleanSeries condition = BooleanSeries.forBooleans(false, true, true);
        Series<Integer> s = createSeries(3, 4, 2).filter(condition);
        new SeriesAsserts(s).expectData(4, 2);
    }
}
