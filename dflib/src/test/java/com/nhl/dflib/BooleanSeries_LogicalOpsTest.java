package com.nhl.dflib;

import com.nhl.dflib.unit.BooleanSeriesAsserts;
import org.junit.Test;

public class BooleanSeries_LogicalOpsTest {

    @Test
    public void testAnd() {
        BooleanSeries s = BooleanSeries.forBooleans(true, false, true, false);
        BooleanSeries and = s.and(BooleanSeries.forBooleans(false, true, true, false));
        new BooleanSeriesAsserts(and).expectData(false, false, true, false);
    }

    @Test
    public void testOr() {
        BooleanSeries s = BooleanSeries.forBooleans(true, false, true, false);
        BooleanSeries or = s.or(BooleanSeries.forBooleans(false, true, true, false));
        new BooleanSeriesAsserts(or).expectData(true, true, true, false);
    }

    @Test
    public void testNot() {
        BooleanSeries s = BooleanSeries.forBooleans(true, false, true, false);
        BooleanSeries and = s.not();
        new BooleanSeriesAsserts(and).expectData(false, true, false, true);
    }
}
