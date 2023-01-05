package com.nhl.dflib.exp.datetime;

import com.nhl.dflib.Condition;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.DateExp;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.unit.BoolSeriesAsserts;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.nhl.dflib.Exp.$col;
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
    public void testYear() {
        NumExp<Integer> year = $date(0).year();
        Series<LocalDate> s = Series.of(LocalDate.of(2007, 1, 8), LocalDate.of(2008, 1, 1), LocalDate.of(2009, 1, 8));
        new SeriesAsserts(year.eval(s)).expectData(2007, 2008, 2009);
    }

    @Test
    public void testMonth() {
        NumExp<Integer> month = $date(0).month();
        Series<LocalDate> s = Series.of(LocalDate.of(2007, 2, 8), LocalDate.of(2008, 1, 1), LocalDate.of(2009, 12, 8));
        new SeriesAsserts(month.eval(s)).expectData(2, 1, 12);
    }

    @Test
    public void testDay() {
        NumExp<Integer> day = $date(0).day();
        Series<LocalDate> s = Series.of(LocalDate.of(2007, 1, 8), LocalDate.of(2008, 1, 1), LocalDate.of(2009, 1, 6));
        new SeriesAsserts(day.eval(s)).expectData(8, 1, 6);
    }

    @Test
    public void testEq() {
        Condition eq = $date("b").eq($col("c"));

        DataFrame df = DataFrame.newFrame("b", "c").foldByRow(
                LocalDate.of(2007, 1, 8), LocalDate.of(2007, 1, 8),
                LocalDate.of(2009, 2, 2), LocalDate.of(2005, 3, 5));

        new BoolSeriesAsserts(eq.eval(df)).expectData(true, false);
    }

    @Test
    public void testNe() {
        Condition ne = $date("b").ne($col("c"));

        DataFrame df = DataFrame.newFrame("b", "c").foldByRow(
                LocalDate.of(2007, 1, 8), LocalDate.of(2007, 1, 8),
                LocalDate.of(2009, 2, 2), LocalDate.of(2005, 3, 5));

        new BoolSeriesAsserts(ne.eval(df)).expectData(false, true);
    }

    @Test
    public void testLt() {
        Condition lt = $date("b").lt($col("c"));

        DataFrame df = DataFrame.newFrame("b", "c").foldByRow(
                LocalDate.of(2007, 1, 8), LocalDate.of(2009, 1, 8),
                LocalDate.of(2009, 2, 2), LocalDate.of(2005, 3, 5));

        new BoolSeriesAsserts(lt.eval(df)).expectData(true, false);
    }

    @Test
    public void testLtVal() {
        Condition lt = $date(0).lt(LocalDate.of(2008, 1, 1));
        Series<LocalDate> s = Series.of(LocalDate.of(2007, 1, 8), LocalDate.of(2008, 1, 1), LocalDate.of(2009, 1, 8));
        new BoolSeriesAsserts(lt.eval(s)).expectData(true, false, false);
    }

    @Test
    public void testLeVal() {
        Condition le = $date(0).le(LocalDate.of(2008, 1, 1));
        Series<LocalDate> s = Series.of(LocalDate.of(2007, 1, 8), LocalDate.of(2008, 1, 1), LocalDate.of(2009, 1, 8));
        new BoolSeriesAsserts(le.eval(s)).expectData(true, true, false);
    }

    @Test
    public void testPlusDays() {
        DateExp exp = $date(0).plusDays(4);

        Series<LocalDate> s = Series.of(LocalDate.of(2007, 1, 8), LocalDate.of(2011, 12, 31));
        new SeriesAsserts(exp.eval(s)).expectData(LocalDate.of(2007, 1, 12), LocalDate.of(2012, 1, 4));
    }

    @Test
    public void testOpsChain() {
        DateExp exp = $date(0).plusDays(4).plusWeeks(1);

        Series<LocalDate> s = Series.of(LocalDate.of(2007, 1, 8), LocalDate.of(2011, 12, 31));
        new SeriesAsserts(exp.eval(s)).expectData(LocalDate.of(2007, 1, 19), LocalDate.of(2012, 1, 11));
    }
}
