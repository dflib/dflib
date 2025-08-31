package org.dflib.exp.str;

import org.dflib.DataFrame;
import org.dflib.StrExp;
import org.dflib.exp.BaseExpTest;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$str;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StrExp_UpperTest extends BaseExpTest {

    @Test
    public void toQL() {
        StrExp exp = $str("a").upper();
        assertEquals("upper(a)", exp.toQL());
    }

    @Test
    public void eval() {
        StrExp exp = $str("b").upper();

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "abc",
                4, "XyZ",
                7, null,
                8, "");

        new SeriesAsserts(exp.eval(df)).expectData("ABC", "XYZ", null, "");
    }

    @Test
    public void testEquals() {
        assertExpEquals(
                $str("a").upper(),
                $str("a").upper(),
                $str("a").trim());
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                $str("a").upper(),
                $str("a").upper(),
                $str("a").trim());
    }
}
