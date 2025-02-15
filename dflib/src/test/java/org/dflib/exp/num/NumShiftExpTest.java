package org.dflib.exp.num;

import org.dflib.Series;
import org.dflib.exp.BaseExpTest;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class NumShiftExpTest extends BaseExpTest {

    @Test
    public void toQL() {
        assertEquals("shift(a,1,null)", $int("a").shift(1).toQL());
        assertEquals("shift(a,1,99)", $long("a").shift(1, 99L).toQL());
        assertEquals("shift(a,1,2.56)", $decimal("a").shift(1, new BigDecimal("2.56")).toQL());
    }

    @Test
    public void shiftOffset() {
        Series<Integer> s1 = Series.of(1, 2, 3);
        new SeriesAsserts(s1.eval($int(0).shift(1))).expectData(null, 1, 2);
        new SeriesAsserts(s1.eval($int(0).shift(2))).expectData(null, null, 1);
        new SeriesAsserts(s1.eval($int(0).shift(6))).expectData(null, null, null);
        new SeriesAsserts(s1.eval($int(0).shift(100))).expectData(null, null, null);

        new SeriesAsserts(s1.eval($int(0).shift(-1))).expectData(2, 3, null);
        new SeriesAsserts(s1.eval($int(0).shift(-100))).expectData(null, null, null);
    }

    @Test
    public void shift_NumChain() {
        Series<Integer> s1 = Series.of(1, 2, 3);
        new SeriesAsserts(s1.eval($int(0).shift(1).add(3))).expectData(null, 4, 5);
    }

    @Test
    public void testEquals() {
        assertExpEquals(
                $int("a").shift(1),
                $int("a").shift(1),
                $int("b").shift(1));

        assertExpEquals(
                $int("a").shift(1),
                $int("a").shift(1),
                $int("a").shift(2));
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                $int("a").shift(1),
                $int("a").shift(1),
                $int("b").shift(1));

        assertExpHashCode(
                $int("a").shift(1),
                $int("a").shift(1),
                $int("a").shift(2));
    }
}
