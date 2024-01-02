package org.dflib.series;

import org.dflib.series.TrueSeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrueSeriesTest {

    @Test
    public void getBoolean() {
        TrueSeries s = new TrueSeries(2);
        assertEquals(true, s.getBool(0));
        assertEquals(true, s.getBool(1));
    }
}
