package org.dflib.series;

import org.dflib.BooleanSeries;
import org.dflib.Series;
import org.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

public class BooleanIndexedSeries_ConcatBoolTest {

    @Test
    public void none() {
        BooleanIndexedSeries s = new BooleanIndexedSeries(
                Series.ofBool(true, true, false, false, true),
                Series.ofInt(1, 3, 4));

        assertSame(s, s.concatBool());
    }

    @Test
    public void self() {
        BooleanIndexedSeries s = new BooleanIndexedSeries(
                Series.ofBool(true, true, false, false, true),
                Series.ofInt(1, 3, 4));

        BooleanSeries c = s.concatBool(s);
        new BoolSeriesAsserts(c).expectData(true, false, true, true, false, true);
    }

    @Test
    public void many() {
        BooleanIndexedSeries s1 = new BooleanIndexedSeries(
                Series.ofBool(true, true, false, false, true),
                Series.ofInt(0, 2));

        BooleanIndexedSeries s2 = new BooleanIndexedSeries(
                Series.ofBool(true, true, false, false, true),
                Series.ofInt(2, 3));

        BooleanIndexedSeries s3 = new BooleanIndexedSeries(
                Series.ofBool(true, true, false, false, true),
                Series.ofInt(1, 1));

        BooleanSeries c = s1.concatBool(s2, s3);
        new BoolSeriesAsserts(c).expectData(true, false, false, false, true, true);
    }
}
