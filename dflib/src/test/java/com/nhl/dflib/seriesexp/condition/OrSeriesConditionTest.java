package com.nhl.dflib.seriesexp.condition;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
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
}
