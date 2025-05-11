package org.dflib.exp;

import org.dflib.Series;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$col;
import static org.dflib.Exp.$str;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShiftExpTest extends BaseExpTest {

    @Test
    public void toQL() {
        assertEquals("shift(a,1,null)", $col("a").shift(1).toQL());
        assertEquals("shift(a,1,'X')", $col("a").shift(1, "X").toQL());
        assertEquals("shift(a,1,5)", $col("a").shift(1, 5).toQL());
    }

    @Test
    public void shiftOffset() {
        Series<String> s1 = Series.of("a", "b", "c");
        new SeriesAsserts(s1.map($str("a").shift(1))).expectData(null, "a", "b");
        new SeriesAsserts(s1.map($str("a").shift(2))).expectData(null, null, "a");
        new SeriesAsserts(s1.map($str("a").shift(6))).expectData(null, null, null);
        new SeriesAsserts(s1.map($str("a").shift(100))).expectData(null, null, null);

        new SeriesAsserts(s1.map($str("a").shift(-1))).expectData("b", "c", null);
        new SeriesAsserts(s1.map($str("a").shift(-100))).expectData(null, null, null);
    }

    @Test
    public void testEquals() {
        assertExpEquals(
                $col("a").shift(1),
                $col("a").shift(1),
                $col("b").shift(1));

        assertExpEquals(
                $col("a").shift(1),
                $col("a").shift(1),
                $col("a").shift(2));
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                $col("a").shift(1),
                $col("a").shift(1),
                $col("b").shift(1));

        assertExpHashCode(
                $col("a").shift(1),
                $col("a").shift(1),
                $col("a").shift(2));
    }
}
