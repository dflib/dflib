package org.dflib;

import org.dflib.series.BooleanArraySeries;
import org.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class BooleanSeries_ConcatBooleanTest {

    @Test
    public void none() {
        BooleanSeries s = new BooleanArraySeries(true, false);
        assertSame(s, s.concatBool());
    }

    @Test
    public void self() {
        BooleanSeries s = new BooleanArraySeries(true, false);
        BooleanSeries c = s.concatBool(s);
        new BoolSeriesAsserts(c).expectData(true, false, true, false);
    }

    @Test
    public void test() {
        BooleanSeries s1 = new BooleanArraySeries(true, false);
        BooleanSeries s2 = new BooleanArraySeries(false, false);
        BooleanSeries s3 = new BooleanArraySeries(true, true);

        BooleanSeries c = s1.concatBool(s2, s3);
        new BoolSeriesAsserts(c).expectData(true, false, false, false, true, true);
    }
}
