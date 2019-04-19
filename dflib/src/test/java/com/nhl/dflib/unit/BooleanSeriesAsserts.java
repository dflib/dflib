package com.nhl.dflib.unit;

import com.nhl.dflib.BooleanSeries;

import static org.junit.Assert.*;

public class BooleanSeriesAsserts {

    private boolean[] data;

    public BooleanSeriesAsserts(BooleanSeries series) {
        assertNotNull("Series is null", series);

        this.data = new boolean[series.size()];
        series.copyToBoolean(data, 0, 0, series.size());
    }

    public BooleanSeriesAsserts expectData(boolean... expectedValues) {

        for (int i = 0; i < expectedValues.length; i++) {

            boolean a = data[i];
            boolean e = expectedValues[i];
            assertEquals("Unexpected value at " + i, e, a);
        }

        return this;
    }
}
