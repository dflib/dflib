package org.dflib.exp.bool;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.unit.BoolSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoolColumnTest {

    @Test
    public void getColumnName() {
        assertEquals("a", $bool("a").getColumnName());
        assertEquals("bool(0)", $bool(0).getColumnName());
        assertEquals("a b", $bool("a b").getColumnName());
    }

    @Test
    public void toQL() {
        assertEquals("a", $bool("a").toQL());
        assertEquals("`bool(0)`", $bool(0).toQL());
        assertEquals("`a b`", $bool("a b").toQL());
    }

    @Test
    public void name_DataFrame() {
        DataFrame df = DataFrame.foldByRow("a", "b").of();
        assertEquals("b", $bool("b").getColumnName(df));
        assertEquals("a", $bool(0).getColumnName(df));
    }

    @Test
    public void eval() {
        Condition c = $bool("b");

        BooleanSeries s = Series.ofBool(false, true, true);
        new BoolSeriesAsserts(c.eval(s)).expectData(false, true, true);
    }

    @Test
    public void as() {
        Condition c = $bool("b");
        assertEquals("b", c.getColumnName(DataFrame.empty()));
        assertEquals("c", c.as("c").getColumnName(DataFrame.empty()));
    }

    @Test
    public void cumSum_Empty() {
        NumExp<Integer> exp = $bool("b").cumSum();

        Series<Boolean> s = Series.of();
        new SeriesAsserts(exp.eval(s)).expectData();
    }

    @Test
    public void cumSum() {
        NumExp<Integer> exp = $bool("b").cumSum();

        Series<Boolean> s = Series.of(false, true, true, null, false, true);
        new SeriesAsserts(exp.eval(s)).expectData(
                0,
                1,
                2,

                // TODO: inconsistency - there should be a "null" in this position.
                //  unlike numeric columns that support nulls, "$bool()" is a "Condition",
                //  that can't have nulls, and will internally convert all nulls to "false"..
                //  Perhaps we need a distinction between a "condition" and a "boolean value expression"?
                2,

                2,
                3
        );
    }
}
