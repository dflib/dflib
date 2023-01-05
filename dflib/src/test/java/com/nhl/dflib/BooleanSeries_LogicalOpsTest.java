package com.nhl.dflib;

import com.nhl.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

public class BooleanSeries_LogicalOpsTest {

    @Test
    public void testAndAll() {
        BooleanSeries and = BooleanSeries.andAll(
                Series.ofBool(true, false, true, false),
                Series.ofBool(false, true, true, false));
        new BoolSeriesAsserts(and).expectData(false, false, true, false);
    }

    @Test
    public void testOrAll() {
        BooleanSeries or = BooleanSeries.orAll(
                Series.ofBool(true, false, true, false),
                Series.ofBool(false, true, true, false));
        new BoolSeriesAsserts(or).expectData(true, true, true, false);
    }

    @Test
    public void testAnd() {
        BooleanSeries s = Series.ofBool(true, false, true, false);
        BooleanSeries and = s.and(Series.ofBool(false, true, true, false));
        new BoolSeriesAsserts(and).expectData(false, false, true, false);
    }

    @Test
    public void testOr() {
        BooleanSeries s = Series.ofBool(true, false, true, false);
        BooleanSeries or = s.or(Series.ofBool(false, true, true, false));
        new BoolSeriesAsserts(or).expectData(true, true, true, false);
    }

    @Test
    public void testNot() {
        BooleanSeries s = Series.ofBool(true, false, true, false);
        BooleanSeries and = s.not();
        new BoolSeriesAsserts(and).expectData(false, true, false, true);
    }
}
