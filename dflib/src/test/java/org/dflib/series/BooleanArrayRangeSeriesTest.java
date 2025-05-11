package org.dflib.series;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Deprecated
public class BooleanArrayRangeSeriesTest {

    @Test
    public void getBoolean_Offset() {
        BooleanArrayRangeSeries s = new BooleanArrayRangeSeries(new boolean[]{true, false, true, false}, 1, 2);
        assertFalse(s.getBool(0));
        assertTrue(s.getBool(1));
    }

    @Test
    public void getBoolean_Offset_OutOfBounds() {
        BooleanArrayRangeSeries s = new BooleanArrayRangeSeries(new boolean[]{true, false, true, false}, 1, 2);
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> s.getBool(2));
    }
}
