package org.dflib.series;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrueSeriesTest {

    @Test
    public void getBoolean() {
        TrueSeries s = new TrueSeries(2);
        assertEquals(true, s.getBool(0));
        assertEquals(true, s.getBool(1));
    }

    @Test
    public void cumSum() {
        TrueSeries s = new TrueSeries(4);
        new SeriesAsserts(s.cumSum()).expectData(1, 2, 3, 4);
    }
}
