package org.dflib.exp.str;

import org.dflib.DataFrame;
import org.dflib.NumExp;
import org.dflib.exp.BaseExpTest;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$str;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StrExp_LenTest extends BaseExpTest {

    @Test
    public void toQL() {
        NumExp<Integer> exp = $str("a").len();
        assertEquals("len(a)", exp.toQL());
    }

    @Test
    public void eval() {
        NumExp<Integer> exp = $str("b").len();

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "---",
                4, "----",
                7, null,
                8, "");

        new SeriesAsserts(exp.eval(df)).expectData(3, 4, null, 0);
    }

    @Test
    public void testEquals() {
        assertExpEquals(
                $str("a").len(),
                $str("a").len(),
                $str("a").trim());
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                $str("a").len(),
                $str("a").len(),
                $str("a").trim());
    }
}
