package org.dflib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FloatSeries_StdDevTest {

    @Test
    public void stdDev_Population() {

        FloatSeries series = Series.ofFloat(3, 28, 15, -4, 3, 11);

//		Validated results with python:
//
//		In [1]: import numpy as np
//		In [2]: a = np.array([3, 28, 15, -4, 3, 11])
//		In [3]: np.std(a)
//		Out[3]: 10.338708279513881
        assertEquals(10.338708279513881d, series.stdDev(true), 0.00001d);
        assertEquals(10.338708279513881d, series.stdDev(), 0.00001d);
    }

    @Test
    public void stdDev_Sample() {

        FloatSeries series = Series.ofFloat(3, 28, 15, -4, 3, 11);

//		Validated results with python:
//
//		In [1]: import numpy as np
//		In [2]: a = np.array([3, 28, 15, -4, 3, 11])
//		In [3]: np.std(a, ddof=1)
//		Out[3]: 11.325487480310358

        double stdDev = series.stdDev(false);
        assertEquals(11.325487480310358d, stdDev, 0.00001d);
    }

}
