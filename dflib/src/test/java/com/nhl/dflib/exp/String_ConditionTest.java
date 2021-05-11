package com.nhl.dflib.exp;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.unit.BooleanSeriesAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.*;

public class String_ConditionTest {

    @Test
    public void testIsNull() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                "1",
                "4",
                null,
                "5");

        BooleanSeries s = $col("a").isNull().eval(df);
        new BooleanSeriesAsserts(s).expectData(false, false, true, false);
    }

    @Test
    public void testIsNotNull() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                "1",
                "4",
                null,
                "5");

        BooleanSeries s = $col("a").isNotNull().eval(df);
        new BooleanSeriesAsserts(s).expectData(true, true, false, true);
    }

    @Test
    public void testEq() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                "1", "1",
                "4", "5");

        BooleanSeries s = $col("b").eq($col("a")).eval(df);
        new BooleanSeriesAsserts(s).expectData(true, false);
    }

    @Test
    public void testNe() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                "1", "1",
                "4", "5");

        BooleanSeries s = $col("b").ne($col("a")).eval(df);
        new BooleanSeriesAsserts(s).expectData(false, true);
    }
}
