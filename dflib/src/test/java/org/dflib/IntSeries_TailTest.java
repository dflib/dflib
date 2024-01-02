package org.dflib;

import org.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class IntSeries_TailTest {

    @Test
    public void tail2() {
        IntSeries s = Series.ofInt(3, 4, 2).tail(2);
        new IntSeriesAsserts(s).expectData(4, 2);
    }

    @Test
    public void tail1() {
        IntSeries s = Series.ofInt(3, 4, 2).tail(1);
        new IntSeriesAsserts(s).expectData(2);
    }

    @Test
    public void zero() {
        IntSeries s = Series.ofInt(3, 4, 2).tail(0);
        new IntSeriesAsserts(s).expectData();
    }

    @Test
    public void outOfBounds() {
        IntSeries s = Series.ofInt(3, 4, 2).tail(4);
        new IntSeriesAsserts(s).expectData(3, 4, 2);
    }

    @Test
    public void negative() {
        IntSeries s = Series.ofInt(3, 4, 2).tail(-2);
        new IntSeriesAsserts(s).expectData(3);
    }
}
