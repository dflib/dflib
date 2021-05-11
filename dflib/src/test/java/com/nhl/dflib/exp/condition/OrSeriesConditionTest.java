package com.nhl.dflib.exp.condition;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrSeriesConditionTest {

    @Test
    public void testFirstMatch() {
        OrSeriesCondition c = new OrSeriesCondition(Exp.$bool(0), Exp.$bool(1));

        DataFrame df0 = DataFrame.newFrame("t1", "f1")
                .columns(BooleanSeries.forBooleans(false, false), BooleanSeries.forBooleans(false, false));
        assertEquals(-1, c.firstMatch(df0));

        DataFrame df1 = DataFrame.newFrame("t1", "f1")
                .columns(BooleanSeries.forBooleans(true, true), BooleanSeries.forBooleans(false, false));
        assertEquals(0, c.firstMatch(df1));

        DataFrame df2 = DataFrame.newFrame("t1", "f1")
                .columns(BooleanSeries.forBooleans(false, false), BooleanSeries.forBooleans(false, true));
        assertEquals(1, c.firstMatch(df2));
    }

    @Test
    public void testFirstMatch_Series() {
        OrSeriesCondition c = new OrSeriesCondition(Exp.$bool(0), Exp.$bool(1));

        Series<Boolean> s0 = BooleanSeries.forBooleans(false, false);
        assertEquals(-1, c.firstMatch(s0));

        Series<Boolean> s1 = BooleanSeries.forBooleans(true, false);
        assertEquals(0, c.firstMatch(s1));

        Series<Boolean> s2 = BooleanSeries.forBooleans(false, true);
        assertEquals(1, c.firstMatch(s2));
    }
}
