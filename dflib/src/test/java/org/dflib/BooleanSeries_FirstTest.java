package org.dflib;

import org.dflib.Series;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BooleanSeries_FirstTest {

    @Test
    public void firstTrue() {
        assertEquals(-1, Series.ofBool().firstTrue());
        assertEquals(0, Series.ofBool(true, true, true).firstTrue());
        assertEquals(2, Series.ofBool(false, false, true).firstTrue());
        assertEquals(-1, Series.ofBool(false, false, false).firstTrue());
    }
}
