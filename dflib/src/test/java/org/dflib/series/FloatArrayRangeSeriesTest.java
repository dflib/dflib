package org.dflib.series;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FloatArrayRangeSeriesTest {

    @Test
    public void getFloat_Offset() {
        FloatArrayRangeSeries s = new FloatArrayRangeSeries(new float[]{1f, 2.1f, 3.2f, 4f}, 1, 2);
        assertEquals(2.1, s.getFloat(0), 0.001);
        assertEquals(3.2, s.getFloat(1), 0.001);
    }

    @Test
    public void getFloat_Offset_OutOfBounds() {
        FloatArrayRangeSeries s = new FloatArrayRangeSeries(new float[]{1.f, 2.1f, 3.2f, 4f}, 1, 2);

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> s.getFloat(2));
    }
}
