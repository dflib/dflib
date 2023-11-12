package com.nhl.dflib.series;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DoubleArrayRangeSeriesTest {

    @Test
    public void getDouble_Offset() {
        DoubleArrayRangeSeries s = new DoubleArrayRangeSeries(new double[]{1., 2.1, 3.2, 4}, 1, 2);
        assertEquals(2.1, s.getDouble(0), 0.001);
        assertEquals(3.2, s.getDouble(1), 0.001);
    }

    @Test
    public void getDouble_Offset_OutOfBounds() {
        DoubleArrayRangeSeries s = new DoubleArrayRangeSeries(new double[]{1., 2.1, 3.2, 4}, 1, 2);

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> s.getDouble(2));
    }
}
