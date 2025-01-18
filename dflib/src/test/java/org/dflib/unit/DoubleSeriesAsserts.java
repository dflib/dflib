package org.dflib.unit;

import org.dflib.DoubleSeries;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DoubleSeriesAsserts {

    private final double[] data;
    private double assertionDelta;

    public DoubleSeriesAsserts(DoubleSeries series) {
        assertNotNull(series, "Series is null");

        this.data = new double[series.size()];
        series.copyToDouble(data, 0, 0, series.size());
        this.assertionDelta = 0.000000001;
    }

    public DoubleSeriesAsserts delta(double assertionDelta) {
        this.assertionDelta = assertionDelta;
        return this;
    }

    public DoubleSeriesAsserts expectData(double... expectedValues) {

        assertEquals(expectedValues.length, data.length, "Unexpected DoubleSeries length");

        for (int i = 0; i < expectedValues.length; i++) {

            double a = data[i];
            double e = expectedValues[i];
            assertEquals(e, a, assertionDelta, "Unexpected value at " + i);
        }

        return this;
    }
}
