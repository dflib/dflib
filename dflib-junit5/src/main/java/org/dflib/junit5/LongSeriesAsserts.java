package org.dflib.junit5;

import org.dflib.LongSeries;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @since 0.8
 */
public class LongSeriesAsserts {
    private long[] data;

    public LongSeriesAsserts(LongSeries series) {
        assertNotNull(series, "Series is null");

        this.data = new long[series.size()];
        series.copyToLong(data, 0, 0, series.size());
    }

    public LongSeriesAsserts expectData(long... expectedValues) {

        assertEquals(expectedValues.length, data.length, "Unexpected LongSeries length");

        for (int i = 0; i < expectedValues.length; i++) {

            long a = data[i];
            long e = expectedValues[i];
            assertEquals(e, a, "Unexpected value at " + i);
        }

        return this;
    }
}
