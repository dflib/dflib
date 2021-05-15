package com.nhl.dflib.exp;

import com.nhl.dflib.*;
import com.nhl.dflib.unit.BooleanSeriesAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class StrTest {

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

    @Test
    public void testStartsWith() {
        Condition c = $str(0).startsWith("_");

        Series<String> s = Series.forData("a", "_b", "c", "__d");
        new BooleanSeriesAsserts(c.eval(s)).expectData(false, true, false, true);
    }

    @Test
    public void testEndsWith() {
        Condition c = $str(0).endsWith("_");

        Series<String> s = Series.forData("a_", "_b", "c", "__d_");
        new BooleanSeriesAsserts(c.eval(s)).expectData(true, false, false, true);
    }

}
