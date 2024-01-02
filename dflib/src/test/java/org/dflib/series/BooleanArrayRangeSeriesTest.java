package org.dflib.series;

import org.dflib.series.BooleanArrayRangeSeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BooleanArrayRangeSeriesTest {

    @Test
    public void getBoolean_Offset() {
        BooleanArrayRangeSeries s = new BooleanArrayRangeSeries(new boolean[]{true, false, true, false}, 1, 2);
        assertEquals(false, s.getBool(0));
        assertEquals(true, s.getBool(1));
    }

    @Test
    public void getBoolean_Offset_OutOfBounds() {
        BooleanArrayRangeSeries s = new BooleanArrayRangeSeries(new boolean[]{true, false, true, false}, 1, 2);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> s.getBool(2));
    }
}
