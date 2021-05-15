package com.nhl.dflib.exp.func;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.*;

public class IfExpFunctionTest {

    @Test
    public void testMix() {
        Exp<String> exp = ifExp($col("c").eq("x"), $str("a"), $str("b"));

        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                "1", "2", "x",
                null, "5", "y",
                null, "6", "x",
                "7", null, "x",
                "8", "9", "z");

        new SeriesAsserts(exp.eval(df)).expectData("1", "5", null, "7", "9");
    }

    @Test
    public void testAllTrue() {
        Exp<String> exp = ifExp($col("c").eq("x"), $str("a"), $str("b"));

        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                "1", "2", "x",
                null, "5", "x",
                null, "6", "x",
                "7", null, "x",
                "8", "9", "x");

        new SeriesAsserts(exp.eval(df)).expectData("1", null, null, "7", "8");
    }

    @Test
    public void testAllFalse() {
        Exp<String> exp = ifExp($col("c").eq("y"), $str("a"), $str("b"));

        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                "1", "2", "x",
                null, "5", "x",
                null, "6", "x",
                "7", null, "x",
                "8", "9", "x");

        new SeriesAsserts(exp.eval(df)).expectData("2", "5", "6", null, "9");
    }
}
