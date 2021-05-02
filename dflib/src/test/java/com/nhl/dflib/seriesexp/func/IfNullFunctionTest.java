package com.nhl.dflib.seriesexp.func;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.$str;
import static com.nhl.dflib.Exp.ifNull;

public class IfNullFunctionTest {

    @Test
    public void testString() {
        SeriesExp<String> noNulls = ifNull($str("a"), $str("b"));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                "1", "2",
                null, "5",
                null, "6",
                null, null);

        new SeriesAsserts(noNulls.eval(df)).expectData("1", "5", "6", null);
    }

    @Test
    public void testNumber() {
        SeriesExp<String> noNulls = ifNull($str("a"), $str("b"));

        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, 2,
                null, 5,
                8, 6,
                null, 7);

        new SeriesAsserts(noNulls.eval(df)).expectData(1, 5, 8, 7);
    }
}
