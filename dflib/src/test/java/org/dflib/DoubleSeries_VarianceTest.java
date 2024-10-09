package org.dflib;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DoubleSeries_VarianceTest {

	@Test
	public void variance_Population() {

		final DoubleSeries series = Series.ofDouble(3, 28, 15, -4, 3, 11);

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
		assertEquals(106.88888888888887d, series.variance(true));
		assertEquals(106.88888888888887d, series.variance());
	}

	@Test
	public void variance_Sample() {

		final DoubleSeries series = Series.ofDouble(3, 28, 15, -4, 3, 11);

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
		final double stdDev = series.variance(false);
		assertEquals(128.26666666666665d, stdDev);
	}

}
