package com.nhl.dflib;

import com.nhl.dflib.series.BooleanArraySeries;
import com.nhl.dflib.unit.BooleanSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class BooleanSeries_ConcatBooleanTest {

    @Test
    public void test_None() {
        BooleanSeries s = new BooleanArraySeries(true, false);
        assertSame(s, s.concatBoolean());
    }

    @Test
    public void test_Self() {
        BooleanSeries s = new BooleanArraySeries(true, false);
        BooleanSeries c = s.concatBoolean(s);
        new BooleanSeriesAsserts(c).expectData(true, false, true, false);
    }

    @Test
    public void test() {
        BooleanSeries s1 = new BooleanArraySeries(true, false);
        BooleanSeries s2 = new BooleanArraySeries(false, false);
        BooleanSeries s3 = new BooleanArraySeries(true, true);

        BooleanSeries c = s1.concatBoolean(s2, s3);
        new BooleanSeriesAsserts(c).expectData(true, false, false, false, true, true);
    }
}
