package org.dflib;

import org.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

public class BooleanSeries_LogicalOpsTest {

    @Test
    public void andAll() {
        BooleanSeries and = BooleanSeries.andAll(
                Series.ofBool(true, false, true, false),
                Series.ofBool(false, true, true, false));
        new BoolSeriesAsserts(and).expectData(false, false, true, false);
    }

    @Test
    public void orAll() {
        BooleanSeries or = BooleanSeries.orAll(
                Series.ofBool(true, false, true, false),
                Series.ofBool(false, true, true, false));
        new BoolSeriesAsserts(or).expectData(true, true, true, false);
    }

    @Test
    public void and() {
        BooleanSeries s = Series.ofBool(true, false, true, false);
        BooleanSeries and = s.and(Series.ofBool(false, true, true, false));
        new BoolSeriesAsserts(and).expectData(false, false, true, false);
    }

    @Test
    public void or() {
        BooleanSeries s = Series.ofBool(true, false, true, false);
        BooleanSeries or = s.or(Series.ofBool(false, true, true, false));
        new BoolSeriesAsserts(or).expectData(true, true, true, false);
    }

    @Test
    public void not() {
        BooleanSeries s = Series.ofBool(true, false, true, false);
        BooleanSeries and = s.not();
        new BoolSeriesAsserts(and).expectData(false, true, false, true);
    }
}
