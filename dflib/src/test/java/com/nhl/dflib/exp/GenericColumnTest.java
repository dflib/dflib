package com.nhl.dflib.exp;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.unit.BooleanSeriesAsserts;
import org.junit.jupiter.api.Test;

import static com.nhl.dflib.Exp.$col;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class GenericColumnTest {

    @Test
    public void testName() {
        assertEquals("a", $col("a").getName());
        assertEquals("$col(0)", $col(0).getName());
    }

    @Test
    public void testName_DataFrame() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow();
        assertEquals("b", $col("b").getName(df));
        assertEquals("a", $col(0).getName(df));
    }

    @Test
    public void testAs() {
        Exp<?> e = $col("b");
        assertEquals("b", e.getName(mock(DataFrame.class)));
        assertEquals("c", e.as("c").getName(mock(DataFrame.class)));
    }

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
}
