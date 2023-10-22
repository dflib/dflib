package com.nhl.dflib;

import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSeries_TailIntTest {

    @Test
    public void test0() {
        IntSeries s = Series.ofInt(3, 4, 2).tailInt(2);
        new IntSeriesAsserts(s).expectData(4, 2);
    }

    @Test
    public void test1() {
        IntSeries s = Series.ofInt(3, 4, 2).tailInt(1);
        new IntSeriesAsserts(s).expectData(2);
    }

    @Test
    public void test_Zero() {
        IntSeries s = Series.ofInt(3, 4, 2).tailInt(0);
        new IntSeriesAsserts(s).expectData();
    }

    @Test
    public void test_OutOfBounds() {
        IntSeries s = Series.ofInt(3, 4, 2).tailInt(4);
        new IntSeriesAsserts(s).expectData(3, 4, 2);
    }

    @Test
    public void test_Negative() {
        IntSeries s = Series.ofInt(3, 4, 2).tailInt(-2);
        new IntSeriesAsserts(s).expectData(3);
    }
}
