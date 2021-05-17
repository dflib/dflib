package com.nhl.dflib.exp.str;

import com.nhl.dflib.Condition;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.StrExp;
import com.nhl.dflib.unit.BooleanSeriesAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.$str;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class StrColumnTest {

    @Test
    public void testGetColumnName() {
        assertEquals("a", $str("a").getColumnName());
        assertEquals("$str(0)", $str(0).getColumnName());
    }

    @Test
    public void testGetColumnName_DataFrame() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow();
        assertEquals("b", $str("b").getColumnName(df));
        assertEquals("a", $str(0).getColumnName(df));
    }

    @Test
    public void testAs() {
        StrExp e = $str("b");
        assertEquals("b", e.getColumnName(mock(DataFrame.class)));
        assertEquals("c", e.as("c").getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void testEval() {
        StrExp exp = $str("b");

        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                "1", "2", "3",
                "4", "5", "6");

        new SeriesAsserts(exp.eval(df)).expectData("2", "5");
    }

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
