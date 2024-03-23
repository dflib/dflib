package org.dflib.exp.str;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.StrExp;
import org.dflib.TimeExp;
import org.dflib.unit.BoolSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.dflib.Exp.$str;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class StrColumnTest {

    @Test
    public void getColumnName() {
        assertEquals("a", $str("a").getColumnName());
        assertEquals("$str(0)", $str(0).getColumnName());
    }

    @Test
    public void getColumnName_DataFrame() {
        DataFrame df = DataFrame.foldByRow("a", "b").of();
        assertEquals("b", $str("b").getColumnName(df));
        assertEquals("a", $str(0).getColumnName(df));
    }

    @Test
    public void as() {
        StrExp e = $str("b");
        assertEquals("b", e.getColumnName(mock(DataFrame.class)));
        assertEquals("c", e.as("c").getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void eval() {
        StrExp exp = $str("b");

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", "2", "3",
                "4", "5", "6");

        new SeriesAsserts(exp.eval(df)).expectData("2", "5");
    }

    @Test
    public void eq() {
        Condition eq = $str("b").eq($str("a"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                "1", "1",
                "4", "5");

        new BoolSeriesAsserts(eq.eval(df)).expectData(true, false);
    }

    @Test
    public void ne() {
        Condition ne = $str("b").ne($str("a"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                "1", "1",
                "4", "5");

        new BoolSeriesAsserts(ne.eval(df)).expectData(false, true);
    }

    @Test
    public void startsWith() {
        Condition c = $str(0).startsWith("_");

        Series<String> s = Series.of("a", "_b", "c", "__d");
        new BoolSeriesAsserts(c.eval(s)).expectData(false, true, false, true);
    }

    @Test
    public void endsWith() {
        Condition c = $str(0).endsWith("_");

        Series<String> s = Series.of("a_", "_b", "c", "__d_");
        new BoolSeriesAsserts(c.eval(s)).expectData(true, false, false, true);
    }

    @Test
    public void contains() {
        Condition c = $str(0).contains("_");

        Series<String> s = Series.of("a_", "_b", "c", "[_d]");
        new BoolSeriesAsserts(c.eval(s)).expectData(true, true, false, true);
    }

    @Test
    public void matches() {
        Condition c = $str(0).matches("^a.*[0-9]$");

        Series<String> s = Series.of("a", "a9", "abcd0", "__d");
        new BoolSeriesAsserts(c.eval(s)).expectData(false, true, true, false);
    }

    @Test
    public void substr0() {
        StrExp exp = $str(0).substr(0);

        Series<String> s = Series.of(null, "", "a", "ab", "abc", "abcd");
        new SeriesAsserts(exp.eval(s)).expectData(null, "", "a", "ab", "abc", "abcd");
    }

    @Test
    public void substr2() {
        StrExp exp = $str(0).substr(2);

        Series<String> s = Series.of(null, "", "a", "ab", "abc", "abcd");
        new SeriesAsserts(exp.eval(s)).expectData(null, "", "", "", "c", "cd");
    }

    @Test
    public void substr2_Negative() {
        StrExp exp = $str(0).substr(-2);

        Series<String> s = Series.of(null, "", "a", "ab", "abc", "abcd");
        new SeriesAsserts(exp.eval(s)).expectData(null, "", "", "", "bc", "cd");
    }

    @Test
    public void substr_Len() {
        StrExp exp = $str(0).substr(2, 1);

        Series<String> s = Series.of(null, "", "a", "ab", "abc", "abcd");
        new SeriesAsserts(exp.eval(s)).expectData(null, "", "", "", "c", "c");
    }

    @Test
    public void substr_Negative_Len() {
        StrExp exp = $str(0).substr(-2, 1);

        Series<String> s = Series.of(null, "", "a", "ab", "abc", "abcd");
        new SeriesAsserts(exp.eval(s)).expectData(null, "", "", "", "b", "c");
    }

    @Test
    public void castAsBool() {
        Condition c = $str(0).castAsBool();

        Series<String> s = Series.of("true", null, "false", "__d_");
        new BoolSeriesAsserts(c.eval(s)).expectData(true, false, false, false);
    }

    @Test
    public void castAsBool_MapVal() {
        Condition isEven = $str(0).mapVal(s -> s.length() % 2 == 0).castAsBool();

        Series<String> s = Series.of("a", "a9", "abcd0", "__d_");
        new BoolSeriesAsserts(isEven.eval(s)).expectData(false, true, false, true);
    }

    @Test
    public void castAsDate() {
        DateExp date = $str(0).castAsDate();
        Series<String> s = Series.of("2021-01-02", null);
        new SeriesAsserts(date.eval(s)).expectData(LocalDate.of(2021, 1, 2), null);
    }

    @Test
    public void castAsDate_Formatter() {
        DateExp date = $str(0).castAsDate("yyyy MM dd");
        Series<String> s = Series.of("2021 01 02", null);
        new SeriesAsserts(date.eval(s)).expectData(LocalDate.of(2021, 1, 2), null);
    }

    @Test
    public void castAsTime() {
        TimeExp time = $str(0).castAsTime();
        Series<String> s = Series.of("23:59:58", null);
        new SeriesAsserts(time.eval(s)).expectData(LocalTime.of(23, 59, 58), null);
    }

    @Test
    public void castAsDateTime() {
        DateTimeExp date = $str(0).castAsDateTime();
        Series<String> s = Series.of("2021-01-02T11:01:34", null);
        new SeriesAsserts(date.eval(s)).expectData(LocalDateTime.of(2021, 1, 2, 11, 1, 34), null);
    }

    @Test
    public void castAsDateTime_Formatter() {
        DateTimeExp date = $str(0).castAsDateTime("yyyy MM dd HH:mm:ss");
        Series<String> s = Series.of("2021 01 02 11:01:34", null);
        new SeriesAsserts(date.eval(s)).expectData(LocalDateTime.of(2021, 1, 2, 11, 1, 34), null);
    }

    @Test
    public void castAsInt() {
        NumExp<Integer> exp = $str(0).castAsInt();
        Series<String> s = Series.of("123", "34.6", null);
        new SeriesAsserts(exp.eval(s)).expectData(123, 34, null);
    }

    @Test
    public void castAsLong() {
        NumExp<Long> exp = $str(0).castAsLong();
        Series<String> s = Series.of("123", "34.6", null);
        new SeriesAsserts(exp.eval(s)).expectData(123L, 34L, null);
    }

    @Test
    public void castAsDouble() {
        NumExp<Double> exp = $str(0).castAsDouble();
        Series<String> s = Series.of("123", "34.6", null);
        new SeriesAsserts(exp.eval(s)).expectData(123., 34.6, null);
    }
}
