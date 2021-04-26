package com.nhl.dflib.series;

import com.nhl.dflib.unit.BooleanSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrueSeriesTest {

    @Test
    public void testGetBoolean() {
        TrueSeries s = new TrueSeries(2);
        assertEquals(true, s.getBoolean(0));
        assertEquals(true, s.getBoolean(1));
    }

    @Test
    public void testHeaBoolean() {
        new BooleanSeriesAsserts(new TrueSeries(3).headBoolean(2)).expectData(true, true);
        new BooleanSeriesAsserts(new TrueSeries(3).headBoolean(4)).expectData(true, true, true);
    }
}
