package com.nhl.dflib;

import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSeries_TailTest {

    @Test
    public void test0() {
        IntSeries s = Series.ofInt(3, 4, 2).tail(2);
        new IntSeriesAsserts(s).expectData(4, 2);
    }

    @Test
    public void test1() {
        IntSeries s = Series.ofInt(3, 4, 2).tail(1);
        new IntSeriesAsserts(s).expectData(2);
    }

    @Test
    public void test_Zero() {
        IntSeries s = Series.ofInt(3, 4, 2).tail(0);
        new IntSeriesAsserts(s).expectData();
    }

    @Test
    public void test_OutOfBounds() {
        IntSeries s = Series.ofInt(3, 4, 2).tail(4);
        new IntSeriesAsserts(s).expectData(3, 4, 2);
    }

    @Test
    public void test_Negative() {
        IntSeries s = Series.ofInt(3, 4, 2).tail(-2);
        new IntSeriesAsserts(s).expectData(3);
    }
}
