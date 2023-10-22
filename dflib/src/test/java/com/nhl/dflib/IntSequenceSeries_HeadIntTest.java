package com.nhl.dflib;

import com.nhl.dflib.series.IntSequenceSeries;
import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSequenceSeries_HeadIntTest {

    @Test
    public void test() {
        IntSeries s = new IntSequenceSeries(1, 4).headInt(2);
        new IntSeriesAsserts(s).expectData(1, 2);
    }

    @Test
    public void test_Zero() {
        IntSeries s = new IntSequenceSeries(1, 4).headInt(0);
        new IntSeriesAsserts(s).expectData();
    }

    @Test
    public void test_OutOfBounds() {
        IntSeries s = new IntSequenceSeries(1, 4).headInt(4);
        new IntSeriesAsserts(s).expectData(1, 2, 3);
    }
}
