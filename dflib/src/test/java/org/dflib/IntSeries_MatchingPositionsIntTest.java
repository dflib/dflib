package org.dflib;

import org.dflib.series.IntArraySeries;
import org.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSeries_MatchingPositionsIntTest {

    @Test
    public void test() {
        IntSeries s = new IntArraySeries(3, 4, 2).indexInt(i -> i % 2 == 0);
        new IntSeriesAsserts(s).expectData(1, 2);
    }

    @Test
    public void all() {
        IntSeries s = new IntArraySeries(3, 4, 2).indexInt(i -> true);
        new IntSeriesAsserts(s).expectData(0, 1, 2);
    }

    @Test
    public void none() {
        IntSeries s = new IntArraySeries(3, 4, 2).indexInt(i -> false);
        new IntSeriesAsserts(s).expectData();
    }
}
