package com.nhl.dflib.exp.date;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.DateExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.nhl.dflib.Exp.$date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class DateColumnTest {

    @Test
    public void testGetColumnName() {
        assertEquals("a", $date("a").getColumnName());
        assertEquals("$date(0)", $date(0).getColumnName());
    }

    @Test
    public void testGetColumnName_DataFrame() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow();
        assertEquals("b", $date("b").getColumnName(df));
        assertEquals("a", $date(0).getColumnName(df));
    }

    @Test
    public void testEval() {
        DateExp exp = $date("b");

        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                "1", LocalDate.of(2007, 1, 8), LocalDate.of(209, 2, 2),
                "4", LocalDate.of(2011, 11, 9), LocalDate.of(2005, 3, 5));

        new SeriesAsserts(exp.eval(df)).expectData(LocalDate.of(2007, 1, 8), LocalDate.of(2011, 11, 9));
    }

    @Test
    public void testAs() {
        DateExp exp = $date("b");
        assertEquals("b", exp.getColumnName(mock(DataFrame.class)));
        assertEquals("c", exp.as("c").getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void testPlusDays() {
        DateExp exp = $date(0).plusDays(4);

        Series<LocalDate> s = Series.forData(LocalDate.of(2007, 1, 8), LocalDate.of(2011, 12, 31));
        new SeriesAsserts(exp.eval(s)).expectData(LocalDate.of(2007, 1, 12), LocalDate.of(2012, 1, 4));
    }

    @Test
    public void testOpsChain() {
        DateExp exp = $date(0).plusDays(4).plusWeeks(1);

        Series<LocalDate> s = Series.forData(LocalDate.of(2007, 1, 8), LocalDate.of(2011, 12, 31));
        new SeriesAsserts(exp.eval(s)).expectData(LocalDate.of(2007, 1, 19), LocalDate.of(2012, 1, 11));
    }
}
