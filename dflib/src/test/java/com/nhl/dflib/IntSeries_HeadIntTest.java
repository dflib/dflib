package com.nhl.dflib;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.series.IntArraySeries;
import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSeries_HeadIntTest {

    @Test
    public void test() {
        IntSeries s = new IntArraySeries(3, 4, 2).headInt(2);
        new IntSeriesAsserts(s).expectData(3, 4);
    }

    @Test
    public void test_Zero() {
        IntSeries s = new IntArraySeries(3, 4, 2).headInt(0);
        new IntSeriesAsserts(s).expectData();
    }

    @Test
    public void test_OutOfBounds() {
        IntSeries s = new IntArraySeries(3, 4, 2).headInt(4);
        new IntSeriesAsserts(s).expectData(3, 4, 2);
    }
}
