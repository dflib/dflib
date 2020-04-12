package com.nhl.dflib.unit;

import com.nhl.dflib.BooleanSeries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BooleanSeriesAsserts {

    private boolean[] data;

    public BooleanSeriesAsserts(BooleanSeries series) {
        assertNotNull(series, "Series is null");

        this.data = new boolean[series.size()];
        series.copyToBoolean(data, 0, 0, series.size());
    }

    public BooleanSeriesAsserts expectData(boolean... expectedValues) {

        assertEquals(expectedValues.length, data.length, "Unexpected BooleanSeries length");

        for (int i = 0; i < expectedValues.length; i++) {

            boolean a = data[i];
            boolean e = expectedValues[i];
            assertEquals(e, a, "Unexpected value at " + i);
        }

        return this;
    }
}
