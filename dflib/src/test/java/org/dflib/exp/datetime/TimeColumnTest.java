package org.dflib.exp.datetime;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.TimeExp;
import org.dflib.unit.BoolSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class TimeColumnTest {

    @Test
    public void getColumnName() {
        assertEquals("a", $time("a").getColumnName());
        assertEquals("$time(0)", $time(0).getColumnName());
    }

    @Test
    public void getColumnName_DataFrame() {
        DataFrame df = DataFrame.foldByRow("a", "b").of();
        assertEquals("b", $time("b").getColumnName(df));
        assertEquals("a", $time(0).getColumnName(df));
    }

    @Test
    public void eval() {
        TimeExp exp = $time("b");

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", LocalTime.of(3, 12, 11), LocalTime.of(4, 10, 1),
                "4", LocalTime.of(14, 59, 59), LocalTime.of(3, 0, 0));

        new SeriesAsserts(exp.eval(df)).expectData(LocalTime.of(3, 12, 11), LocalTime.of(14, 59, 59));
    }

    @Test
    public void as() {
        TimeExp exp = $time("b");
        assertEquals("b", exp.getColumnName(mock(DataFrame.class)));
        assertEquals("c", exp.as("c").getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void hour() {
        NumExp<Integer> hr = $time(0).hour();
        Series<LocalTime> s = Series.of(LocalTime.of(3, 12, 11), LocalTime.of(4, 10, 1), LocalTime.of(14, 59, 59));
        new SeriesAsserts(hr.eval(s)).expectData(3, 4, 14);
    }

    @Test
    public void minute() {
        NumExp<Integer> min = $time(0).minute();
        Series<LocalTime> s = Series.of(LocalTime.of(3, 12, 11), LocalTime.of(4, 10, 1), LocalTime.of(14, 59, 59));
        new SeriesAsserts(min.eval(s)).expectData(12, 10, 59);
    }

    @Test
    public void second() {
        NumExp<Integer> exp = $time(0).second();
        Series<LocalTime> s = Series.of(LocalTime.of(3, 12, 11), LocalTime.of(4, 10, 1), LocalTime.of(14, 59, 59));
        new SeriesAsserts(exp.eval(s)).expectData(11, 1, 59);
    }

    @Test
    public void millisecond() {
        NumExp<Integer> exp = $time(0).millisecond();
        Series<LocalTime> s = Series.of(LocalTime.of(3, 12, 11, 4_000_000), LocalTime.of(4, 10, 1), LocalTime.of(14, 59, 59, 400_000));
        new SeriesAsserts(exp.eval(s)).expectData(4, 0, 0);
    }

    @Test
    public void plusHours() {
        TimeExp exp = $time(0).plusHours(4);

        Series<LocalTime> s = Series.of(LocalTime.of(3, 12, 11), LocalTime.of(14, 59, 59));
        new SeriesAsserts(exp.eval(s)).expectData(LocalTime.of(7, 12, 11), LocalTime.of(18, 59, 59));
    }

    @Test
    public void plusMilliseconds() {
        TimeExp exp = $time(0).plusMilliseconds(4);

        Series<LocalTime> s = Series.of(LocalTime.of(3, 12, 11), LocalTime.of(14, 59, 59));
        new SeriesAsserts(exp.eval(s)).expectData(LocalTime.of(3, 12, 11, 4_000_000), LocalTime.of(14, 59, 59, 4_000_000));
    }

    @Test
    public void plusNanos() {
        TimeExp exp = $time(0).plusNanos(4);

        Series<LocalTime> s = Series.of(LocalTime.of(3, 12, 11), LocalTime.of(14, 59, 59));
        new SeriesAsserts(exp.eval(s)).expectData(LocalTime.of(3, 12, 11, 4), LocalTime.of(14, 59, 59, 4));
    }

    @Test
    public void opsChain() {
        TimeExp exp = $time(0).plusHours(4).plusSeconds(1);

        Series<LocalTime> s = Series.of(LocalTime.of(3, 12, 11), LocalTime.of(14, 59, 59));
        new SeriesAsserts(exp.eval(s)).expectData(LocalTime.of(7, 12, 12), LocalTime.of(19, 0, 0));
    }

    @Test
    public void eq() {
        Condition eq = $time("b").eq($col("c"));

        DataFrame df = DataFrame.foldByRow("b", "c").of(
                LocalTime.of(3, 12, 11), LocalTime.of(3, 12, 11),
                LocalTime.of(4, 12, 11), LocalTime.of(14, 59, 59));

        new BoolSeriesAsserts(eq.eval(df)).expectData(true, false);
    }

    @Test
    public void eqVal() {
        Condition eq = $time("b").eq(LocalTime.of(4, 10, 1));

        Series<LocalTime> s = Series.of(
                LocalTime.of(3, 12, 11),
                LocalTime.of(4, 10, 1),
                LocalTime.of(14, 59, 59));

        new BoolSeriesAsserts(eq.eval(s)).expectData(false, true, false);
    }

    @Test
    public void eqStrVal() {
        Condition eq = $time("b").eq("04:10:01");

        Series<LocalTime> s = Series.of(
                LocalTime.of(3, 12, 11),
                LocalTime.of(4, 10, 1),
                LocalTime.of(14, 59, 59));

        new BoolSeriesAsserts(eq.eval(s)).expectData(false, true, false);
    }

    @Test
    public void ne() {
        Condition exp = $time("b").ne($col("c"));

        DataFrame df = DataFrame.foldByRow("b", "c").of(
                LocalTime.of(3, 12, 11), LocalTime.of(3, 12, 11),
                LocalTime.of(4, 12, 11), LocalTime.of(14, 59, 59));

        new BoolSeriesAsserts(exp.eval(df)).expectData(false, true);
    }

    @Test
    public void neVal() {
        Condition ne = $time("b").ne(LocalTime.of(4, 10, 1));

        Series<LocalTime> s = Series.of(
                LocalTime.of(3, 12, 11),
                LocalTime.of(4, 10, 1),
                LocalTime.of(14, 59, 59));

        new BoolSeriesAsserts(ne.eval(s)).expectData(true, false, true);
    }

    @Test
    public void neStrVal() {
        Condition ne = $time("b").ne("04:10:01");

        Series<LocalTime> s = Series.of(
                LocalTime.of(3, 12, 11),
                LocalTime.of(4, 10, 1),
                LocalTime.of(14, 59, 59));

        new BoolSeriesAsserts(ne.eval(s)).expectData(true, false, true);
    }

    @Test
    public void lt() {
        Condition lt = $time("b").lt($col("c"));

        DataFrame df = DataFrame.foldByRow("b", "c").of(
                LocalTime.of(3, 12, 11), LocalTime.of(3, 12, 11),
                LocalTime.of(4, 12, 11), LocalTime.of(14, 59, 59));

        new BoolSeriesAsserts(lt.eval(df)).expectData(false, true);
    }

    @Test
    public void ltVal() {
        Condition lt = $time("b").lt(LocalTime.of(4, 10, 1));

        Series<LocalTime> s = Series.of(
                LocalTime.of(3, 12, 11),
                LocalTime.of(4, 10, 1),
                LocalTime.of(14, 59, 59));

        new BoolSeriesAsserts(lt.eval(s)).expectData(true, false, false);
    }

    @Test
    public void ltStrVal() {
        Condition lt = $time("b").lt(LocalTime.of(4, 10, 1));

        Series<LocalTime> s = Series.of(
                LocalTime.of(3, 12, 11),
                LocalTime.of(4, 10, 1),
                LocalTime.of(14, 59, 59));

        new BoolSeriesAsserts(lt.eval(s)).expectData(true, false, false);
    }

    @Test
    public void le() {
        Condition le = $time("b").le($col("c"));

        DataFrame df = DataFrame.foldByRow("b", "c").of(
                LocalTime.of(3, 12, 11), LocalTime.of(3, 12, 11),
                LocalTime.of(4, 12, 11), LocalTime.of(14, 59, 59));

        new BoolSeriesAsserts(le.eval(df)).expectData(true, true);
    }

    @Test
    public void leVal() {
        Condition le = $time("b").le(LocalTime.of(4, 10, 1));

        Series<LocalTime> s = Series.of(
                LocalTime.of(3, 12, 11),
                LocalTime.of(4, 10, 1),
                LocalTime.of(14, 59, 59));

        new BoolSeriesAsserts(le.eval(s)).expectData(true, true, false);
    }

    @Test
    public void leStrVal() {
        Condition le = $time("b").le("04:10:01");

        Series<LocalTime> s = Series.of(
                LocalTime.of(3, 12, 11),
                LocalTime.of(4, 10, 1),
                LocalTime.of(14, 59, 59));

        new BoolSeriesAsserts(le.eval(s)).expectData(true, true, false);
    }

    @Test
    public void betweenVal() {
        Condition le = $time("b").between(LocalTime.of(4, 10, 0), LocalTime.of(8, 10, 0));

        Series<LocalTime> s = Series.of(
                LocalTime.of(3, 12, 11),
                LocalTime.of(4, 10, 1),
                LocalTime.of(6, 10, 1),
                LocalTime.of(8, 10, 1),
                LocalTime.of(14, 59, 59));

        new BoolSeriesAsserts(le.eval(s)).expectData(false, true, true, false, false);
    }

    @Test
    public void betweenStrVal() {
        Condition le = $time("b").between("04:10", "08:10");

        Series<LocalTime> s = Series.of(
                LocalTime.of(3, 12, 11),
                LocalTime.of(4, 10, 1),
                LocalTime.of(6, 10, 1),
                LocalTime.of(8, 10, 1),
                LocalTime.of(14, 59, 59));

        new BoolSeriesAsserts(le.eval(s)).expectData(false, true, true, false, false);
    }

    @Test
    public void shift_plus() {
        TimeExp e = $time("b").shift(-1).shift(-1).plusHours(3);

        Series<LocalTime> s = Series.of(
                LocalTime.of(3, 12, 11),
                LocalTime.of(4, 10, 1),
                LocalTime.of(6, 10, 1),
                LocalTime.of(8, 10, 1));

        new SeriesAsserts(e.eval(s)).expectData(
                LocalTime.of(9, 10, 1),
                LocalTime.of(11, 10, 1),
                null,
                null);
    }
}
