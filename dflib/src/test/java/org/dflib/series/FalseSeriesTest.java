package org.dflib.series;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class FalseSeriesTest {

    @Test
    public void cumSum() {
        FalseSeries s = new FalseSeries(4);
        new SeriesAsserts(s.cumSum()).expectData(0, 0, 0, 0);
    }
}
