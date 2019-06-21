package com.nhl.dflib.test;

import com.nhl.dflib.LongSeries;

import static org.junit.Assert.*;

/**
 * @since 0.6
 */
public class LongSeriesAsserts {
    private long[] data;

    public LongSeriesAsserts(LongSeries series) {
        assertNotNull("Series is null", series);

        this.data = new long[series.size()];
        series.copyToLong(data, 0, 0, series.size());
    }

    public LongSeriesAsserts expectData(long... expectedValues) {

        assertEquals("Unexpected LongSeries length", expectedValues.length, data.length);

        for (int i = 0; i < expectedValues.length; i++) {

            long a = data[i];
            long e = expectedValues[i];
            assertEquals("Unexpected value at " + i, e, a);
        }

        return this;
    }
}
