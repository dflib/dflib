package org.dflib;

import org.dflib.unit.LongSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

@Deprecated
public class LongSeries_ConcatLongTest {

    @Test
    public void none() {
        LongSeries s = Series.ofLong(1, 2);
        assertSame(s, s.concatLong());
    }

    @Test
    public void self() {
        LongSeries s = Series.ofLong(1, 2);
        LongSeries c = s.concatLong(s);
        new LongSeriesAsserts(c).expectData(1, 2, 1, 2);
    }

    @Test
    public void test() {
        LongSeries s1 = Series.ofLong(34, 23);
        LongSeries s2 = Series.ofLong(1, 2);
        LongSeries s3 = Series.ofLong(-1, -6);

        LongSeries c = s1.concatLong(s2, s3);
        new LongSeriesAsserts(c).expectData(34, 23, 1, 2, -1, -6);
    }
}
