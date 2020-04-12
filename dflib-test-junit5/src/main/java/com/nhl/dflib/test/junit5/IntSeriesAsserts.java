package com.nhl.dflib.test.junit5;

import com.nhl.dflib.IntSeries;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @since 0.6
 */
public class IntSeriesAsserts {
    private int[] data;

    public IntSeriesAsserts(IntSeries series) {
        assertNotNull(series, "Series is null");

        this.data = new int[series.size()];
        series.copyToInt(data, 0, 0, series.size());
    }

    public IntSeriesAsserts expectData(int... expectedValues) {

        assertEquals(expectedValues.length, data.length, "Unexpected IntSeries length");

        for (int i = 0; i < expectedValues.length; i++) {

            int a = data[i];
            int e = expectedValues[i];
            assertEquals(e, a, "Unexpected value at " + i);
        }

        return this;
    }
}
