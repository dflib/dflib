package org.dflib.unit;

import org.dflib.FloatSeries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FloatSeriesAsserts {

    private float[] data;

    public FloatSeriesAsserts(FloatSeries series) {
        assertNotNull(series, "Series is null");

        this.data = new float[series.size()];
        series.copyToFloat(data, 0, 0, series.size());
    }

    public FloatSeriesAsserts expectData(float... expectedValues) {

        assertEquals(expectedValues.length, data.length, "Unexpected FloatSeries length");

        for (int i = 0; i < expectedValues.length; i++) {

            float a = data[i];
            float e = expectedValues[i];
            assertEquals(e, a, 0.000000001f, "Unexpected value at " + i);
        }

        return this;
    }
}
