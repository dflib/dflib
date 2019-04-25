package com.nhl.dflib.unit;

import com.nhl.dflib.LongSeries;

import static org.junit.Assert.*;

public class LongSeriesAsserts {
    private long[] data;

    public LongSeriesAsserts(LongSeries series) {
        assertNotNull("Series is null", series);

        this.data = new long[series.size()];
        series.copyToLong(data, 0, 0, series.size());
    }

    public LongSeriesAsserts expectData(long... expectedValues) {

        for (int i = 0; i < expectedValues.length; i++) {

            long a = data[i];
            long e = expectedValues[i];
            assertEquals("Unexpected value at " + i, e, a);
        }

        return this;
    }
}
