package com.nhl.dflib.unit;

import com.nhl.dflib.series.IntSeries;

import static org.junit.Assert.*;

public class IntSeriesAsserts {
    private int[] data;

    public IntSeriesAsserts(IntSeries series) {
        assertNotNull("Series is null", series);

        this.data = new int[series.size()];
        series.copyToInt(data, 0, 0, series.size());
    }

    public IntSeriesAsserts expectData(int... expectedValues) {

        for (int i = 0; i < expectedValues.length; i++) {

            int a = data[i];
            int e = expectedValues[i];
            assertEquals("Unexpected value at " + i, e, a);
        }

        return this;
    }
}
