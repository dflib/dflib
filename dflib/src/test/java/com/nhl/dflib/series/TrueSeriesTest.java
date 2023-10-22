package com.nhl.dflib.series;

import com.nhl.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrueSeriesTest {

    @Test
    public void testGetBoolean() {
        TrueSeries s = new TrueSeries(2);
        assertEquals(true, s.getBool(0));
        assertEquals(true, s.getBool(1));
    }

    @Test
    public void testHeadBoolean() {
        new BoolSeriesAsserts(new TrueSeries(3).head(2)).expectData(true, true);
        new BoolSeriesAsserts(new TrueSeries(3).head(4)).expectData(true, true, true);
    }
}
