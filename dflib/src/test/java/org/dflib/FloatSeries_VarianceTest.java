package org.dflib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FloatSeries_VarianceTest {

    @Test
    public void variance_Population() {

        FloatSeries series = Series.ofFloat(3, 28, 15, -4, 3, 11);

        /**
         * Validated results with python:
         *
         * In [1]: import numpy as np
         *
         * In [2]: a = np.array([3, 28, 15, -4, 3, 11])
         *
         * In [3]: np.var(a)
         *
         * Out[3]: 106.88888888888887
         *
         */
        assertEquals(106.88888888888887d, series.variance(true), 0.00001d);
        assertEquals(106.88888888888887d, series.variance(), 0.00001d);
    }

    @Test
    public void variance_Sample() {

        final FloatSeries series = Series.ofFloat(3, 28, 15, -4, 3, 11);

        /**
         * Validated results with python:
         *
         * In [1]: import numpy as np
         *
         * In [2]: a = np.array([3, 28, 15, -4, 3, 11])
         *
         * In [3]: np.var(a, ddof=1)
         *
         * Out[3]: 128.26666666666665
         */
        double stdDev = series.variance(false);
        assertEquals(128.26666666666665d, stdDev, 0.00001d);
    }

}
