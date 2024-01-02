package org.dflib.exp.datetime;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.unit.BoolSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class DateTimeColumnTest {

    @Test
    public void getColumnName() {
        assertEquals("a", $dateTime("a").getColumnName());
        assertEquals("$dateTime(0)", $dateTime(0).getColumnName());
    }

    @Test
    public void getColumnName_DataFrame() {
        DataFrame df = DataFrame.foldByRow("a", "b").of();
        assertEquals("b", $dateTime("b").getColumnName(df));
        assertEquals("a", $dateTime(0).getColumnName(df));
    }

    @Test
    public void eval() {
        DateTimeExp exp = $dateTime("b");

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", LocalDateTime.of(2007, 1, 8, 1, 2, 3), LocalDate.of(209, 2, 2),
                "4", LocalDateTime.of(2011, 11, 9, 4, 5, 6), LocalDate.of(2005, 3, 5));

        new SeriesAsserts(exp.eval(df)).expectData(
                LocalDateTime.of(2007, 1, 8, 1, 2, 3), LocalDateTime.of(2011, 11, 9, 4, 5, 6));
    }

    @Test
    public void as() {
        DateTimeExp exp = $dateTime("b");
        assertEquals("b", exp.getColumnName(mock(DataFrame.class)));
        assertEquals("c", exp.as("c").getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void year() {
        NumExp<Integer> year = $dateTime(0).year();
        Series<LocalDateTime> s = Series.of(LocalDateTime.of(2007, 1, 8, 1, 2, 3), LocalDateTime.of(2008, 1, 1, 4, 5, 6), LocalDateTime.of(2009, 1, 8, 7, 8, 9));
        new SeriesAsserts(year.eval(s)).expectData(2007, 2008, 2009);
    }

    @Test
    public void month() {
        NumExp<Integer> month = $dateTime(0).month();
        Series<LocalDateTime> s = Series.of(LocalDateTime.of(2007, 2, 8, 1, 2, 3), LocalDateTime.of(2008, 1, 1, 4, 5, 6), LocalDateTime.of(2009, 12, 8, 7, 8, 9));
        new SeriesAsserts(month.eval(s)).expectData(2, 1, 12);
    }

    @Test
    public void day() {
        NumExp<Integer> day = $dateTime(0).day();
        Series<LocalDateTime> s = Series.of(LocalDateTime.of(2007, 2, 8, 1, 2, 3), LocalDateTime.of(2008, 1, 1, 4, 5, 6), LocalDateTime.of(2009, 12, 6, 7, 8, 9));
        new SeriesAsserts(day.eval(s)).expectData(8, 1, 6);
    }

    @Test
    public void hour() {
        NumExp<Integer> hr = $dateTime(0).hour();
        Series<LocalDateTime> s = Series.of(LocalDateTime.of(2007, 2, 8, 3, 12, 11), LocalDateTime.of(2007, 2, 8, 4, 10, 1), LocalDateTime.of(2007, 2, 8, 14, 59, 59));
        new SeriesAsserts(hr.eval(s)).expectData(3, 4, 14);
    }

    @Test
    public void minute() {
        NumExp<Integer> min = $dateTime(0).minute();
        Series<LocalDateTime> s = Series.of(LocalDateTime.of(2007, 2, 8, 3, 12, 11), LocalDateTime.of(2007, 2, 8, 4, 10, 1), LocalDateTime.of(2007, 2, 8, 14, 59, 59));
        new SeriesAsserts(min.eval(s)).expectData(12, 10, 59);
    }

    @Test
    public void second() {
        NumExp<Integer> exp = $dateTime(0).second();
        Series<LocalDateTime> s = Series.of(LocalDateTime.of(2007, 2, 8, 3, 12, 11), LocalDateTime.of(2007, 2, 8, 4, 10, 1), LocalDateTime.of(2007, 2, 8, 14, 59, 59));
        new SeriesAsserts(exp.eval(s)).expectData(11, 1, 59);
    }

    @Test
    public void millisecond() {
        NumExp<Integer> exp = $dateTime(0).millisecond();
        Series<LocalDateTime> s = Series.of(
                LocalDateTime.of(2007, 2, 8, 3, 12, 11, 4_000_000),
                LocalDateTime.of(2007, 2, 8, 4, 10, 1),
                LocalDateTime.of(2007, 2, 8, 14, 59, 59, 400_000));
        new SeriesAsserts(exp.eval(s)).expectData(4, 0, 0);
    }

    @Test
    public void eq() {
        Condition eq = $dateTime("b").eq($col("c"));

        DataFrame df = DataFrame.foldByRow("b", "c").of(
                LocalDateTime.of(2007, 1, 8, 1, 1, 2), LocalDateTime.of(2007, 1, 8, 1, 1, 2),
                LocalDateTime.of(2009, 2, 2, 1, 1, 1), LocalDateTime.of(2005, 3, 5, 1, 1, 1),
                LocalDateTime.of(2009, 3, 5, 1, 1, 1), LocalDateTime.of(2005, 3, 5, 1, 1, 2));

        new BoolSeriesAsserts(eq.eval(df)).expectData(true, false, false);
    }

    @Test
    public void ne() {
        Condition ne = $dateTime("b").ne($col("c"));

        DataFrame df = DataFrame.foldByRow("b", "c").of(
                LocalDateTime.of(2007, 1, 8, 1, 1, 2), LocalDateTime.of(2007, 1, 8, 1, 1, 2),
                LocalDateTime.of(2009, 2, 2, 1, 1, 1), LocalDateTime.of(2005, 3, 5, 1, 1, 1),
                LocalDateTime.of(2009, 3, 5, 1, 1, 1), LocalDateTime.of(2005, 3, 5, 1, 1, 2));

        new BoolSeriesAsserts(ne.eval(df)).expectData(false, true, true);
    }

    @Test
    public void lt() {
        Condition lt = $dateTime("b").lt($col("c"));

        DataFrame df = DataFrame.foldByRow("b", "c").of(
                LocalDateTime.of(2007, 1, 8, 1, 1, 1), LocalDateTime.of(2009, 1, 8, 1, 1, 1),
                LocalDateTime.of(2009, 2, 2, 1, 1, 1), LocalDateTime.of(2005, 3, 5, 1, 1, 1));

        new BoolSeriesAsserts(lt.eval(df)).expectData(true, false);
    }

    @Test
    public void ltVal() {
        Condition lt = $dateTime(0).lt(LocalDateTime.of(2008, 1, 1, 1, 1));
        Series<LocalDateTime> s = Series.of(
                LocalDateTime.of(2007, 1, 8, 1, 1),
                LocalDateTime.of(2008, 1, 1, 1, 1),
                LocalDateTime.of(2009, 1, 8, 1, 1));
        new BoolSeriesAsserts(lt.eval(s)).expectData(true, false, false);
    }

    @Test
    public void leVal() {
        Condition le = $dateTime(0).le(LocalDateTime.of(2008, 1, 1, 1, 1));
        Series<LocalDateTime> s = Series.of(
                LocalDateTime.of(2007, 1, 8, 1, 1),
                LocalDateTime.of(2008, 1, 1, 1, 1),
                LocalDateTime.of(2009, 1, 8, 1, 1));
        new BoolSeriesAsserts(le.eval(s)).expectData(true, true, false);
    }

    @Test
    public void betweenVal() {
        Condition le = $dateTime(0).between(LocalDateTime.of(2008, 1, 1, 1, 1), LocalDateTime.of(2012, 1, 1, 1, 1));
        Series<LocalDateTime> s = Series.of(
                LocalDateTime.of(2008, 1, 1, 1, 0),
                LocalDateTime.of(2008, 1, 1, 1, 1),
                LocalDateTime.of(2008, 5, 8, 5, 6),
                LocalDateTime.of(2012, 1, 1, 1, 1),
                LocalDateTime.of(2012, 1, 1, 1, 2));

        new BoolSeriesAsserts(le.eval(s)).expectData(false, true, true, true, false);
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
}
