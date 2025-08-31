package org.dflib.exp.str;

import org.dflib.DataFrame;
import org.dflib.StrExp;
import org.dflib.exp.BaseExpTest;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$str;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StrExp_LowerTest extends BaseExpTest {

    @Test
    public void toQL() {
        StrExp exp = $str("a").lower();
        assertEquals("lower(a)", exp.toQL());
    }

    @Test
    public void eval() {
        StrExp exp = $str("b").lower();

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "abc",
                4, "XyZ",
                7, null,
                8, "");

        new SeriesAsserts(exp.eval(df)).expectData("abc", "xyz", null, "");
    }

    @Test
    public void testEquals() {
        assertExpEquals(
                $str("a").lower(),
                $str("a").lower(),
                $str("a").trim());
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                $str("a").lower(),
                $str("a").lower(),
                $str("a").trim());
    }
}
