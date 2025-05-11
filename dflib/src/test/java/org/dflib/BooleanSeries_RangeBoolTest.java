package org.dflib;

import org.dflib.series.BooleanBitsetSeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BooleanSeries_RangeBoolTest {

    @Test
    public void rangeBool() {
        BooleanSeries s = Series.ofBool(true, false, false, true);

        BooleanSeries range = s.rangeBool(2, 4);

        assertEquals(2, range.size());
        assertInstanceOf(BooleanBitsetSeries.class, range);
        assertFalse(range.getBool(0));
        assertTrue(range.getBool(1));
    }
}