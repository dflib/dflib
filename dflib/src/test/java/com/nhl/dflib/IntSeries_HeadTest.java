package com.nhl.dflib;

import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSeries_HeadTest {

    @Test
    public void test() {
        IntSeries s = Series.ofInt(3, 4, 2).head(2);
        new IntSeriesAsserts(s).expectData(3, 4);
    }

    @Test
    public void test_Zero() {
        IntSeries s = Series.ofInt(3, 4, 2).head(0);
        new IntSeriesAsserts(s).expectData();
    }

    @Test
    public void test_OutOfBounds() {
        IntSeries s = Series.ofInt(3, 4, 2).head(4);
        new IntSeriesAsserts(s).expectData(3, 4, 2);
    }

    @Test
    public void test_Negative() {
        IntSeries s = Series.ofInt(3, 4, 2).head(-2);
        new IntSeriesAsserts(s).expectData(2);
    }
}
