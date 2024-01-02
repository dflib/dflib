package org.dflib.exp.condition;

import org.dflib.BooleanSeries;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$bool;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class BoolColumnTest {

    @Test
    public void getColumnName() {
        assertEquals("a", $bool("a").getColumnName());
        assertEquals("$bool(0)", $bool(0).getColumnName());
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
        assertEquals("b", c.getColumnName(mock(DataFrame.class)));
        assertEquals("c", c.as("c").getColumnName(mock(DataFrame.class)));
    }
}
