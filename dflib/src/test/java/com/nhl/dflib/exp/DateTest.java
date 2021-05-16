package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.DateExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.nhl.dflib.Exp.$date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class DateTest {

    @Test
    public void testReadColumn() {
        DateExp exp = $date("b");

        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                "1", LocalDate.of(2007, 1, 8), LocalDate.of(209, 2, 2),
                "4", LocalDate.of(2011, 11, 9), LocalDate.of(2005, 3, 5));

        new SeriesAsserts(exp.eval(df)).expectData(LocalDate.of(2007, 1, 8), LocalDate.of(2011, 11, 9));
    }

    @Test
    public void testNamed() {
        DateExp exp = $date("b");
        assertEquals("b", exp.getName(mock(DataFrame.class)));
        assertEquals("c", exp.as("c").getName(mock(DataFrame.class)));
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
