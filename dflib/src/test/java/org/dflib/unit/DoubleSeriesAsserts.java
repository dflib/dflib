package org.dflib.unit;

import org.dflib.DoubleSeries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DoubleSeriesAsserts {

    private double[] data;

    public DoubleSeriesAsserts(DoubleSeries series) {
        assertNotNull(series, "Series is null");

        this.data = new double[series.size()];
        series.copyToDouble(data, 0, 0, series.size());
    }

    public DoubleSeriesAsserts expectData(double... expectedValues) {

        assertEquals(expectedValues.length, data.length, "Unexpected DoubleSeries length");

        for (int i = 0; i < expectedValues.length; i++) {

            double a = data[i];
            double e = expectedValues[i];
            assertEquals(e, a, 0.000000001, "Unexpected value at " + i);
        }

        return this;
    }
}
