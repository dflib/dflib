package com.nhl.dflib.unit;

import com.nhl.dflib.DoubleSeries;

import static org.junit.Assert.*;

public class DoubleSeriesAsserts {

    private double[] data;

    public DoubleSeriesAsserts(DoubleSeries series) {
        assertNotNull("Series is null", series);

        this.data = new double[series.size()];
        series.copyToDouble(data, 0, 0, series.size());
    }

    public DoubleSeriesAsserts expectData(double... expectedValues) {

        for (int i = 0; i < expectedValues.length; i++) {

            double a = data[i];
            double e = expectedValues[i];
            assertEquals("Unexpected value at " + i, e, a, 0.000000001);
        }

        return this;
    }
}
