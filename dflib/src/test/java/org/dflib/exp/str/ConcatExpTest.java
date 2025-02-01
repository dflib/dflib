package org.dflib.exp.str;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.StrExp;
import org.dflib.exp.ExpBaseTest;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ConcatExpTest extends ExpBaseTest {

    @Test
    public void toQL() {
        StrExp exp = Exp.concat($str("a"), $val("X"), $str(0));
        assertEquals("concat(a,'X',$str(0))", exp.toQL());
    }

    @Test
    public void concat() {
        StrExp exp1 = Exp.concat($str("b"), $int("a"));
        StrExp exp2 = Exp.concat("_", $str("b"), "]");

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                1, "2", "3",
                4, "5", "6",
                7, null, null,
                8, "", "9");

        new SeriesAsserts(exp1.eval(df)).expectData("21", "54", null, "8");
        new SeriesAsserts(exp2.eval(df)).expectData("_2]", "_5]", null, "_]");
    }

    @Test
    public void equalsHashCode() {
        Exp<?> e1 = Exp.concat($str("a"), $str("b"), $str("c"));
        Exp<?> e2 = Exp.concat($str("a"), $str("b"), $str("c"));
        Exp<?> e3 = Exp.concat($str("a"), $str("b"), $str("c"));

        Exp<?> different1 = Exp.concat($str("x"), $str("b"), $str("c"));
        Exp<?> different2 = Exp.concat($str("a"), $str("b"));
        Exp<?> different3 = Exp.concat($str("a"), $str("b"), $int("c"));

        assertEqualsContract(e1, e2, e3);
        assertNotEquals(e1, different1, "Should not equal expression with a different argument");
        assertNotEquals(e1, different2, "Should not equal expression with fewer arguments");
        assertNotEquals(e1, different3, "Should not equal expression with different argument types");
    }
}
