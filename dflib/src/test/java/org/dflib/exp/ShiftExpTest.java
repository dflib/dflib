package org.dflib.exp;

import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ShiftExpTest extends ExpBaseTest {

    @Test
    public void toQL() {
        assertEquals("shift(a,1,null)", Exp.$col("a").shift(1).toQL());
        assertEquals("shift(a,1,'X')", Exp.$col("a").shift(1, "X").toQL());
        assertEquals("shift(a,1,5)", Exp.$col("a").shift(1, 5).toQL());
    }

    @Test
    public void shiftOffset() {
        Series<String> s1 = Series.of("a", "b", "c");
        new SeriesAsserts(s1.eval(Exp.$str("a").shift(1))).expectData(null, "a", "b");
        new SeriesAsserts(s1.eval(Exp.$str("a").shift(2))).expectData(null, null, "a");
        new SeriesAsserts(s1.eval(Exp.$str("a").shift(6))).expectData(null, null, null);
        new SeriesAsserts(s1.eval(Exp.$str("a").shift(100))).expectData(null, null, null);

        new SeriesAsserts(s1.eval(Exp.$str("a").shift(-1))).expectData("b", "c", null);
        new SeriesAsserts(s1.eval(Exp.$str("a").shift(-100))).expectData(null, null, null);
    }

    @Test
    public void equalsHashCode() {
        Exp<?> e1 = Exp.$col("a").shift(1);
        Exp<?> e2 = Exp.$col("a").shift(1);
        Exp<?> e3 = Exp.$col("a").shift(1);
        Exp<?> different1 = Exp.$col("b").shift(1);
        Exp<?> different2 = Exp.$col("a").shift(2);

        assertEqualsContract(e1, e2, e3);
        assertNotEquals(e1, different1);
        assertNotEquals(e1, different2);
    }
}
