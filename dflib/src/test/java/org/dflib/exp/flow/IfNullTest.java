package org.dflib.exp.flow;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.exp.BaseExpTest;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class IfNullTest extends BaseExpTest {

    @Test
    public void col() {
        Exp<?> noNulls = ifNull($col("a"), $col("b"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                "1", "2",
                null, "5",
                null, "6",
                null, null);

        new SeriesAsserts(noNulls.eval(df)).expectData("1", "5", "6", null);
    }

    @Test
    public void string() {
        Exp<String> noNulls = ifNull($str("a"), $str("b"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                "1", "2",
                null, "5",
                null, "6",
                null, null);

        new SeriesAsserts(noNulls.eval(df)).expectData("1", "5", "6", null);
    }

    @Test
    public void number() {
        Exp<?> noNulls = ifNull($int("a"), $int("b"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 2,
                null, 5,
                8, 6,
                null, 7);

        new SeriesAsserts(noNulls.eval(df)).expectData(1, 5, 8, 7);
    }

    @Test
    public void testEquals() {
        assertExpEquals(
                ifNull($int("a"), $int(1)),
                ifNull($int("a"), $int(1)),
                ifNull($int("b"), $int(1)));

        assertExpEquals(
                ifNull($int("a"), $int(1)),
                ifNull($int("a"), $int(1)),
                ifNull($int("a"), $int(2)));
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                ifNull($int("a"), $int(1)),
                ifNull($int("a"), $int(1)),
                ifNull($int("b"), $int(1)));

        assertExpHashCode(
                ifNull($int("a"), $int(1)),
                ifNull($int("a"), $int(1)),
                ifNull($int("a"), $int(2)));
    }

}
