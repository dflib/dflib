package org.dflib.exp.str;

import org.dflib.DataFrame;
import org.dflib.StrExp;
import org.dflib.exp.BaseExpTest;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConcatExpTest extends BaseExpTest {

    @Test
    public void toQL() {
        StrExp exp = concat($str("a"), $val("X"), $str(0));
        assertEquals("concat(a,'X',$str(0))", exp.toQL());
    }

    @Test
    public void test() {
        StrExp exp1 = concat($str("b"), $int("a"));
        StrExp exp2 = concat("_", $str("b"), "]");

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                1, "2", "3",
                4, "5", "6",
                7, null, null,
                8, "", "9");

        new SeriesAsserts(exp1.eval(df)).expectData("21", "54", null, "8");
        new SeriesAsserts(exp2.eval(df)).expectData("_2]", "_5]", null, "_]");
    }

    @Test
    public void testEquals() {
        assertExpEquals(
                concat($str("a"), $str("b"), $str("c")),
                concat($str("a"), $str("b"), $str("c")),
                concat($str("x"), $str("b"), $str("c")));

        assertExpEquals(
                concat($str("a"), $val("b"), $str("c")),
                concat($str("a"), "b", $str("c")),
                concat($str("x"), $val("b"), $str("c")));


        assertExpEquals(
                concat($str("a"), $str("b"), $str("c")),
                concat($str("a"), $str("b"), $str("c")),
                concat($str("a"), $str("b")));

        assertExpEquals(
                concat($str("a"), $str("b"), $str("c")),
                concat($str("a"), $str("b"), $str("c")),
                concat($str("a"), $str("b"), $int("c")));
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                concat($str("a"), $str("b"), $str("c")),
                concat($str("a"), $str("b"), $str("c")),
                concat($str("x"), $str("b"), $str("c")));

        assertExpHashCode(
                concat($str("a"), $val("b"), $str("c")),
                concat($str("a"), "b", $str("c")),
                concat($str("x"), $val("b"), $str("c")));

        assertExpHashCode(
                concat($str("a"), $str("b"), $str("c")),
                concat($str("a"), $str("b"), $str("c")),
                concat($str("a"), $str("b")));

        assertExpHashCode(
                concat($str("a"), $str("b"), $str("c")),
                concat($str("a"), $str("b"), $str("c")),
                concat($str("a"), $str("b"), $int("c")));
    }
}
