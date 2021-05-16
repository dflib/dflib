package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.StrExp;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class StrExpTest {

    @Test
    public void testReadColumn() {
        StrExp exp = $str("b");

        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                "1", "2", "3",
                "4", "5", "6");

        new SeriesAsserts(exp.eval(df)).expectData("2", "5");
    }

    @Test
    public void testNamed() {
        StrExp e = $str("b");
        assertEquals("b", e.getName(mock(DataFrame.class)));
        assertEquals("c", e.as("c").getName(mock(DataFrame.class)));
    }

    @Test
    public void testConcat() {
        Exp<String> exp1 = concat($str("b"), $int("a"));
        Exp<String> exp2 = concat("_", $str("b"), "]");

        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                1, "2", "3",
                4, "5", "6",
                7, null, null,
                8, "", "9");

        new SeriesAsserts(exp1.eval(df)).expectData("21", "54", null, "8");
        new SeriesAsserts(exp2.eval(df)).expectData("_2]", "_5]", null, "_]");
    }
}
