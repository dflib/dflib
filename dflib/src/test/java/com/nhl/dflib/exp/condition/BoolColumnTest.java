package com.nhl.dflib.exp.condition;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.Condition;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.unit.BooleanSeriesAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.$bool;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class BoolColumnTest {

    @Test
    public void testName() {
        assertEquals("a", $bool("a").getName());
        assertEquals("$bool(0)", $bool(0).getName());
    }

    @Test
    public void testName_DataFrame() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow();
        assertEquals("b", $bool("b").getName(df));
        assertEquals("a", $bool(0).getName(df));
    }

    @Test
    public void testEval() {
        Condition c = $bool("b");

        BooleanSeries s = BooleanSeries.forBooleans(false, true, true);
        new BooleanSeriesAsserts(c.eval(s)).expectData(false, true, true);
    }

    @Test
    public void testAs() {
        Condition c = $bool("b");
        assertEquals("b", c.getName(mock(DataFrame.class)));
        assertEquals("c", c.as("c").getName(mock(DataFrame.class)));
    }
}
