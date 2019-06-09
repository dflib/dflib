package com.nhl.dflib;

import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

@RunWith(Parameterized.class)
public class Series_ValueCountsTest extends BaseObjectSeriesTest {

    public Series_ValueCountsTest(SeriesTypes seriesType) {
        super(seriesType);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return ALL_SERIES_TYPES;
    }

    @Test
    public void testValueCounts() {
        DataFrame counts = createSeries("a", "b", "a", "a", "c").valueCounts();

        new DFAsserts(counts, "value", "count")
                .expectHeight(3)
                .expectRow(0, "a", 3)
                .expectRow(1, "b", 1)
                .expectRow(2, "c", 1);
    }

    @Test
    public void testValueCounts_Nulls() {
        DataFrame counts = createSeries("a", "b", "a", "a", null, "c").valueCounts();

        new DFAsserts(counts, "value", "count")
                .expectHeight(3)
                .expectRow(0, "a", 3)
                .expectRow(1, "b", 1)
                .expectRow(2, "c", 1);

    }
}
