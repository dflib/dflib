package org.dflib.exp.flow;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.exp.BaseExpTest;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class IfExpTest extends BaseExpTest {

    @Test
    public void col() {
        Exp<?> exp = ifExp($col("c").eq("x"), $col("a"), $col("b"));

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", "2", "x",
                null, "5", "y",
                null, "6", "x",
                "7", null, "x",
                "8", "9", "z");

        new SeriesAsserts(exp.eval(df)).expectData("1", "5", null, "7", "9");
    }

    @Test
    public void mix() {
        Exp<String> exp = ifExp($col("c").eq("x"), $str("a"), $str("b"));

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", "2", "x",
                null, "5", "y",
                null, "6", "x",
                "7", null, "x",
                "8", "9", "z");

        new SeriesAsserts(exp.eval(df)).expectData("1", "5", null, "7", "9");
    }

    @Test
    public void allTrue() {
        Exp<String> exp = ifExp($col("c").eq("x"), $str("a"), $str("b"));

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", "2", "x",
                null, "5", "x",
                null, "6", "x",
                "7", null, "x",
                "8", "9", "x");

        new SeriesAsserts(exp.eval(df)).expectData("1", null, null, "7", "8");
    }

    @Test
    public void allFalse() {
        Exp<String> exp = ifExp($col("c").eq("y"), $str("a"), $str("b"));

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", "2", "x",
                null, "5", "x",
                null, "6", "x",
                "7", null, "x",
                "8", "9", "x");

        new SeriesAsserts(exp.eval(df)).expectData("2", "5", "6", null, "9");
    }

    @Test
    public void testEquals() {
        assertExpEquals(
                ifExp($col("a").eq("test"), $int(1), $int(2)),
                ifExp($col("a").eq("test"), $int(1), $int(2)),
                ifExp($col("a").eq("test"), $int(1), $int(3)));
    }

    @Test
    public void testEquals_Nested() {
        assertExpEquals(
                ifExp(
                        ifExp($bool("a"), $int(1), $int(2)).castAsBool(),
                        ifExp($bool("b"), $int(3), $int(4)),
                        $int(5)),
                ifExp(
                        ifExp($bool("a"), $int(1), $int(2)).castAsBool(),
                        ifExp($bool("b"), $int(3), $int(4)),
                        $int(5)),
                ifExp(
                        ifExp($bool("a"), $int(1), $int(2)).castAsBool(),
                        ifExp($bool("b"), $int(3), $int(4)),
                        $int(6)));
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                ifExp($col("a").eq("test"), $int(1), $int(2)),
                ifExp($col("a").eq("test"), $int(1), $int(2)),
                ifExp($col("a").eq("test"), $int(1), $int(3)));
    }

}
