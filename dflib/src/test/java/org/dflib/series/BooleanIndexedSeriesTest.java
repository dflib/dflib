package org.dflib.series;

import org.dflib.BooleanSeries;
import org.dflib.Series;
import org.dflib.unit.BoolSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

public class BooleanIndexedSeriesTest {

    @Test
    public void of() {
        BooleanSeries s = (BooleanSeries) BooleanIndexedSeries.of(
                Series.ofBool(true, true, false, false, true),
                Series.ofInt(1, 3, 4));

        new BoolSeriesAsserts(s).expectData(true, false, true);
    }

    @Test
    public void of_NegativeIndices() {
        Series<Boolean> s = BooleanIndexedSeries.of(
                Series.ofBool(true, true, false, false, true),
                Series.ofInt(1, 3, -4));

        new SeriesAsserts(s).expectData(true, false, null);
    }
}
