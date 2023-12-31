package com.nhl.dflib;

import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BooleanSeries_SelectTest {

    @Test
    public void positional() {
        Series<Boolean> s = Series.ofBool(true, false, true).select(2, 1);
        new SeriesAsserts(s).expectData(true, false);
        assertTrue(s instanceof BooleanSeries);
    }

    @Test
    public void positional_Empty() {
        Series<Boolean> s = Series.ofBool(true, false, true).select();
        new SeriesAsserts(s).expectData();
        assertTrue(s instanceof BooleanSeries);
    }

    @Test
    public void position_OutOfBounds() {
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> Series.ofBool(true, false, true).select(0, 3).materialize());
    }

    @Test
    public void positional_Nulls() {
        Series<Boolean> s = Series.ofBool(true, false, true).select(2, 1, -1);
        new SeriesAsserts(s).expectData(true, false, null);
        assertFalse(s instanceof BooleanSeries);
    }

    @Test
    public void booleanCondition() {
        BooleanSeries condition = Series.ofBool(false, true, true);
        Series<Boolean> s = Series.ofBool(true, false, true).select(condition);
        new SeriesAsserts(s).expectData(false, true);
        Assertions.assertTrue(s instanceof BooleanSeries);
    }
}
