package org.dflib.exp.datetime;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.DateExp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.unit.BoolSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DateColumnTest {

    @Test
    public void getColumnName() {
        assertEquals("a", $date("a").getColumnName());
        assertEquals("date(0)", $date(0).getColumnName());
        assertEquals("a b", $date("a b").getColumnName());
    }

    @Test
    public void getColumnName_DataFrame() {
        DataFrame df = DataFrame.foldByRow("a", "b").of();
        assertEquals("b", $date("b").getColumnName(df));
        assertEquals("a", $date(0).getColumnName(df));
    }

    @Test
    public void toQL() {
        assertEquals("a", $date("a").toQL());
        assertEquals("`date(0)`", $date(0).toQL());
        assertEquals("`a b`", $date("a b").toQL());
    }

    @Test
    public void eval() {
        DateExp exp = $date("b");

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", LocalDate.of(2007, 1, 8), LocalDate.of(209, 2, 2),
                "4", LocalDate.of(2011, 11, 9), LocalDate.of(2005, 3, 5));

        new SeriesAsserts(exp.eval(df)).expectData(LocalDate.of(2007, 1, 8), LocalDate.of(2011, 11, 9));
    }

    @Test
    public void as() {
        DateExp exp = $date("b");
        assertEquals("b", exp.getColumnName(DataFrame.empty()));
        assertEquals("c", exp.as("c").getColumnName(DataFrame.empty()));
    }

    @Test
    public void year() {
        NumExp<Integer> year = $date(0).year();
        Series<LocalDate> s = Series.of(LocalDate.of(2007, 1, 8), LocalDate.of(2008, 1, 1), LocalDate.of(2009, 1, 8));
        new SeriesAsserts(year.eval(s)).expectData(2007, 2008, 2009);
    }

    @Test
    public void month() {
        NumExp<Integer> month = $date(0).month();
        Series<LocalDate> s = Series.of(LocalDate.of(2007, 2, 8), LocalDate.of(2008, 1, 1), LocalDate.of(2009, 12, 8));
        new SeriesAsserts(month.eval(s)).expectData(2, 1, 12);
    }

    @Test
    public void day() {
        NumExp<Integer> day = $date(0).day();
        Series<LocalDate> s = Series.of(LocalDate.of(2007, 1, 8), LocalDate.of(2008, 1, 1), LocalDate.of(2009, 1, 6));
        new SeriesAsserts(day.eval(s)).expectData(8, 1, 6);
    }

    @Test
    public void eq() {
        Condition eq = $date("b").eq($col("c"));

        DataFrame df = DataFrame.foldByRow("b", "c").of(
                LocalDate.of(2007, 1, 8), LocalDate.of(2007, 1, 8),
                LocalDate.of(2009, 2, 2), LocalDate.of(2005, 3, 5));

        new BoolSeriesAsserts(eq.eval(df)).expectData(true, false);
    }

    @Test
    public void eqVal() {
        Condition eq = $date("b").eq(LocalDate.of(2007, 1, 8));

        Series<LocalDate> s = Series.of(
                LocalDate.of(2007, 1, 8),
                LocalDate.of(2008, 1, 1),
                LocalDate.of(2009, 1, 8));

        new BoolSeriesAsserts(eq.eval(s)).expectData(true, false, false);
    }

    @Test
    public void eqStrVal() {
        Condition eq = $date("b").eq("2009-01-08");

        Series<LocalDate> s = Series.of(
                LocalDate.of(2007, 1, 8),
                LocalDate.of(2008, 1, 1),
                LocalDate.of(2009, 1, 8));

        new BoolSeriesAsserts(eq.eval(s)).expectData(false, false, true);
    }

    @Test
    public void ne() {
        Condition ne = $date("b").ne($col("c"));

        DataFrame df = DataFrame.foldByRow("b", "c").of(
                LocalDate.of(2007, 1, 8), LocalDate.of(2007, 1, 8),
                LocalDate.of(2009, 2, 2), LocalDate.of(2005, 3, 5));

        new BoolSeriesAsserts(ne.eval(df)).expectData(false, true);
    }

    @Test
    public void neVal() {
        Condition ne = $date("b").ne(LocalDate.of(2007, 1, 8));

        Series<LocalDate> s = Series.of(
                LocalDate.of(2007, 1, 8),
                LocalDate.of(2008, 1, 1),
                LocalDate.of(2009, 1, 8));

        new BoolSeriesAsserts(ne.eval(s)).expectData(false, true, true);
    }

    @Test
    public void neStrVal() {
        Condition ne = $date("b").ne("2009-01-08");

        Series<LocalDate> s = Series.of(
                LocalDate.of(2007, 1, 8),
                LocalDate.of(2008, 1, 1),
                LocalDate.of(2009, 1, 8));

        new BoolSeriesAsserts(ne.eval(s)).expectData(true, true, false);
    }

    @Test
    public void lt() {
        Condition lt = $date("b").lt($col("c"));

        DataFrame df = DataFrame.foldByRow("b", "c").of(
                LocalDate.of(2007, 1, 8), LocalDate.of(2009, 1, 8),
                LocalDate.of(2009, 2, 2), LocalDate.of(2005, 3, 5));

        new BoolSeriesAsserts(lt.eval(df)).expectData(true, false);
    }

    @Test
    public void ltVal() {
        Condition lt = $date(0).lt(LocalDate.of(2008, 1, 1));
        Series<LocalDate> s = Series.of(LocalDate.of(2007, 1, 8), LocalDate.of(2008, 1, 1), LocalDate.of(2009, 1, 8));
        new BoolSeriesAsserts(lt.eval(s)).expectData(true, false, false);
    }

    @Test
    public void ltStrVal() {
        Condition lt = $date(0).lt("2008-01-01");
        Series<LocalDate> s = Series.of(LocalDate.of(2007, 1, 8), LocalDate.of(2008, 1, 1), LocalDate.of(2009, 1, 8));
        new BoolSeriesAsserts(lt.eval(s)).expectData(true, false, false);
    }

    @Test
    public void leVal() {
        Condition le = $date(0).le(LocalDate.of(2008, 1, 1));
        Series<LocalDate> s = Series.of(LocalDate.of(2007, 1, 8), LocalDate.of(2008, 1, 1), LocalDate.of(2009, 1, 8));
        new BoolSeriesAsserts(le.eval(s)).expectData(true, true, false);
    }

    @Test
    public void leStrVal() {
        Condition le = $date(0).le("2008-01-01");
        Series<LocalDate> s = Series.of(LocalDate.of(2007, 1, 8), LocalDate.of(2008, 1, 1), LocalDate.of(2009, 1, 8));
        new BoolSeriesAsserts(le.eval(s)).expectData(true, true, false);
    }

    @Test
    public void betweenVal() {
        Condition le = $date(0).between(LocalDate.of(2008, 1, 1), LocalDate.of(2012, 1, 1));
        Series<LocalDate> s = Series.of(
                LocalDate.of(2007, 12, 31),
                LocalDate.of(2008, 1, 1),
                LocalDate.of(2008, 5, 8),
                LocalDate.of(2012, 1, 1),
                LocalDate.of(2012, 1, 2));

        new BoolSeriesAsserts(le.eval(s)).expectData(false, true, true, true, false);
    }

    @Test
    public void betweenStrVal() {
        Condition le = $date(0).between("2008-01-01", "2012-01-01");
        Series<LocalDate> s = Series.of(
                LocalDate.of(2007, 12, 31),
                LocalDate.of(2008, 1, 1),
                LocalDate.of(2008, 5, 8),
                LocalDate.of(2012, 1, 1),
                LocalDate.of(2012, 1, 2));

        new BoolSeriesAsserts(le.eval(s)).expectData(false, true, true, true, false);
    }

    @Test
    public void notBetweenVal() {
        Condition le = $date(0).notBetween(LocalDate.of(2008, 1, 1), LocalDate.of(2012, 1, 1));
        Series<LocalDate> s = Series.of(
                LocalDate.of(2007, 12, 31),
                LocalDate.of(2008, 1, 1),
                LocalDate.of(2008, 5, 8),
                LocalDate.of(2012, 1, 1),
                LocalDate.of(2012, 1, 2));

        new BoolSeriesAsserts(le.eval(s)).expectData(true, false, false, false, true);
    }

    @Test
    public void notBetweenStrVal() {
        Condition le = $date(0).notBetween("2008-01-01", "2012-01-01");
        Series<LocalDate> s = Series.of(
                LocalDate.of(2007, 12, 31),
                LocalDate.of(2008, 1, 1),
                LocalDate.of(2008, 5, 8),
                LocalDate.of(2012, 1, 1),
                LocalDate.of(2012, 1, 2));

        new BoolSeriesAsserts(le.eval(s)).expectData(true, false, false, false, true);
    }

    @Test
    public void plusDays() {
        DateExp exp = $date(0).plusDays(4);

        Series<LocalDate> s = Series.of(LocalDate.of(2007, 1, 8), LocalDate.of(2011, 12, 31));
        new SeriesAsserts(exp.eval(s)).expectData(LocalDate.of(2007, 1, 12), LocalDate.of(2012, 1, 4));
    }

    @Test
    public void opsChain() {
        DateExp exp = $date(0).plusDays(4).plusWeeks(1);

        Series<LocalDate> s = Series.of(LocalDate.of(2007, 1, 8), LocalDate.of(2011, 12, 31));
        new SeriesAsserts(exp.eval(s)).expectData(LocalDate.of(2007, 1, 19), LocalDate.of(2012, 1, 11));
    }

    @Test
    public void shift_plus() {
        DateExp e = $date(0).shift(-1).plusDays(3);

        Series<LocalDate> s = Series.of(
                LocalDate.of(2007, 1, 8),
                LocalDate.of(2011, 12, 31));

        new SeriesAsserts(e.eval(s)).expectData(LocalDate.of(2012, 1, 3), null);
    }
}
