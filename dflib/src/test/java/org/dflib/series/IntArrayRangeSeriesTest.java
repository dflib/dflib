package org.dflib.series;

import org.dflib.series.IntArrayRangeSeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IntArrayRangeSeriesTest {

    @Test
    public void getInt_Offset() {
        IntArrayRangeSeries s = new IntArrayRangeSeries(new int[]{1, 2, 3, 4}, 1, 2);
        assertEquals(2, s.getInt(0));
        assertEquals(3, s.getInt(1));
    }

    @Test
    public void getInt_Offset_OutOfBounds() {
        IntArrayRangeSeries s = new IntArrayRangeSeries(new int[]{1, 2, 3, 4}, 1, 2);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> s.getInt(2));
    }
}
