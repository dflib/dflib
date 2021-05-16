package com.nhl.dflib.exp;

import com.nhl.dflib.Condition;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.unit.BooleanSeriesAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.$str;

public class StrExp_ConditionTest {

    @Test
    public void testEq() {
        Condition eq = $str("b").eq($str("a"));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                "1", "1",
                "4", "5");

        new BooleanSeriesAsserts(eq.eval(df)).expectData(true, false);
    }

    @Test
    public void testNe() {
        Condition ne = $str("b").ne($str("a"));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                "1", "1",
                "4", "5");

        new BooleanSeriesAsserts(ne.eval(df)).expectData(false, true);
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

    @Test
    public void testMatches() {
        Condition c = $str(0).matches("^a.*[0-9]$");

        Series<String> s = Series.forData("a", "a9", "abcd0", "__d");
        new BooleanSeriesAsserts(c.eval(s)).expectData(false, true, true, false);
    }
}
