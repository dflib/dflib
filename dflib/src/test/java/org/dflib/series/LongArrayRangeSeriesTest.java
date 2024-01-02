package org.dflib.series;

import org.dflib.series.LongArrayRangeSeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LongArrayRangeSeriesTest {

    @Test
    public void getLong_Offset() {
        LongArrayRangeSeries s = new LongArrayRangeSeries(new long[]{1, 2, 3, 4}, 1, 2);
        assertEquals(2, s.getLong(0));
        assertEquals(3, s.getLong(1));
    }

    @Test
    public void getLong_Offset_OutOfBounds() {
        LongArrayRangeSeries s = new LongArrayRangeSeries(new long[]{1, 2, 3, 4}, 1, 2);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> s.getLong(2));
    }
}
