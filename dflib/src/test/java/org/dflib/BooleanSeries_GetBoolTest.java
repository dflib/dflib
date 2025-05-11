package org.dflib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class BooleanSeries_GetBoolTest {

    @Test
    public void getBool() {
        BooleanSeries s = Series.ofBool(true, false);
        assertTrue(s.getBool(0));
        assertFalse(s.getBool(1));
    }
}
