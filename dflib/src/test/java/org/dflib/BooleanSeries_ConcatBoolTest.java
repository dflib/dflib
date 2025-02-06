package org.dflib;

import org.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class BooleanSeries_ConcatBoolTest {

    @Test
    public void none() {
        BooleanSeries s = Series.ofBool(true, false);
        assertSame(s, s.concatBool());
    }

    @Test
    public void self() {
        BooleanSeries s = Series.ofBool(true, false);
        BooleanSeries c = s.concatBool(s);
        new BoolSeriesAsserts(c).expectData(true, false, true, false);
    }

    @Test
    public void multi() {
        BooleanSeries s1 = Series.ofBool(true, false);
        BooleanSeries s2 = Series.ofBool(false, false);
        BooleanSeries s3 = Series.ofBool(true, true, true);

        BooleanSeries c = s1.concatBool(s2, s3);
        new BoolSeriesAsserts(c).expectData(true, false, false, false, true, true, true);
    }
}
