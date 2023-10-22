package com.nhl.dflib.series;

import com.nhl.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

public class TrueSeries_TailTest {

    @Test
    public void test() {
        new BoolSeriesAsserts(new TrueSeries(3).tail(2)).expectData(true, true);
    }

    @Test
    public void test_Zero() {
        new BoolSeriesAsserts(new TrueSeries(3).tail(0)).expectData();
    }

    @Test
    public void test_OutOfBounds() {
        new BoolSeriesAsserts(new TrueSeries(3).tail(4)).expectData(true, true, true);
    }

    @Test
    public void test_Negative() {
        new BoolSeriesAsserts(new TrueSeries(3).tail(-2)).expectData(true);
    }
}
