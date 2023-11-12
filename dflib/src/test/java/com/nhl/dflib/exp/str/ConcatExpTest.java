package com.nhl.dflib.exp.str;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.StrExp;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConcatExpTest {

    @Test
    public void toQL() {
        StrExp exp = Exp.concat($str("a"), $val("X"), $str(0));
        assertEquals("concat(a, 'X', $str(0))", exp.toQL());
    }

    @Test
    public void concat() {
        StrExp exp1 = Exp.concat($str("b"), $int("a"));
        StrExp exp2 = Exp.concat("_", $str("b"), "]");

        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "2", "3",
                4, "5", "6",
                7, null, null,
                8, "", "9");

        new SeriesAsserts(exp1.eval(df)).expectData("21", "54", null, "8");
        new SeriesAsserts(exp2.eval(df)).expectData("_2]", "_5]", null, "_]");
    }
}
