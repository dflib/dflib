package org.dflib.exp.datetime;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.NumExp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.Series;
import org.dflib.unit.BoolSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.dflib.Exp.$col;
import static org.dflib.Exp.$offsetDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class OffsetDateTimeColumnTest {

    @Test
    public void getColumnName() {
        assertEquals("a", $offsetDateTime("a").getColumnName());
        assertEquals("$offsetDateTime(0)", $offsetDateTime(0).getColumnName());
    }

    @Test
    public void getColumnName_DataFrame() {
        DataFrame df = DataFrame.foldByRow("a", "b").of();
        assertEquals("b", $offsetDateTime("b").getColumnName(df));
        assertEquals("a", $offsetDateTime(0).getColumnName(df));
    }

    @Test
    public void eval() {
        OffsetDateTimeExp exp = $offsetDateTime("b");

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", OffsetDateTime.of(LocalDateTime.of(2007, 1, 8, 1, 2, 3), ZoneOffset.ofHours(5)), LocalDate.of(209, 2, 2),
                "4", OffsetDateTime.of(LocalDateTime.of(2011, 11, 9, 4, 5, 6), ZoneOffset.ofHours(6)), LocalDate.of(2005, 3, 5));

        new SeriesAsserts(exp.eval(df)).expectData(
                OffsetDateTime.of(LocalDateTime.of(2007, 1, 8, 1, 2, 3), ZoneOffset.ofHours(5)),
                OffsetDateTime.of(LocalDateTime.of(2011, 11, 9, 4, 5, 6), ZoneOffset.ofHours(6)));
    }

    @Test
    public void as() {
        OffsetDateTimeExp exp = $offsetDateTime("b");
        assertEquals("b", exp.getColumnName(mock(DataFrame.class)));
        assertEquals("c", exp.as("c").getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void year() {
        NumExp<Integer> year = $offsetDateTime(0).year();
        Series<OffsetDateTime> s = Series.of(
                OffsetDateTime.of(2007, 1, 8, 1, 2, 3, 0, ZoneOffset.ofHours(1)),
                OffsetDateTime.of(2008, 1, 1, 4, 5, 6, 0, ZoneOffset.ofHours(2)),
                OffsetDateTime.of(2009, 1, 8, 7, 8, 9, 0, ZoneOffset.ofHours(3)));
        new SeriesAsserts(year.eval(s)).expectData(2007, 2008, 2009);
    }

    @Test
    public void month() {
        NumExp<Integer> month = $offsetDateTime(0).month();
        Series<OffsetDateTime> s = Series.of(
                OffsetDateTime.of(2007, 2, 8, 1, 2, 3, 0, ZoneOffset.ofHours(1)),
                OffsetDateTime.of(2008, 1, 1, 4, 5, 6, 0, ZoneOffset.ofHours(2)),
                OffsetDateTime.of(2009, 12, 8, 7, 8, 9, 0, ZoneOffset.ofHours(3)));
        new SeriesAsserts(month.eval(s)).expectData(2, 1, 12);
    }

    @Test
    public void day() {
        NumExp<Integer> day = $offsetDateTime(0).day();
        Series<OffsetDateTime> s = Series.of(
                OffsetDateTime.of(2007, 2, 8, 1, 2, 3, 0, ZoneOffset.ofHours(1)),
                OffsetDateTime.of(2008, 1, 1, 4, 5, 6, 0, ZoneOffset.ofHours(2)),
                OffsetDateTime.of(2009, 12, 6, 7, 8, 9, 0, ZoneOffset.ofHours(3)));
        new SeriesAsserts(day.eval(s)).expectData(8, 1, 6);
    }

    @Test
    public void hour() {
        NumExp<Integer> hr = $offsetDateTime(0).hour();
        Series<OffsetDateTime> s = Series.of(
                OffsetDateTime.of(2007, 2, 8, 3, 2, 3, 0, ZoneOffset.ofHours(1)),
                OffsetDateTime.of(2008, 1, 1, 4, 5, 6, 0, ZoneOffset.ofHours(2)),
                OffsetDateTime.of(2009, 12, 8, 14, 8, 9, 0, ZoneOffset.ofHours(3)));
        new SeriesAsserts(hr.eval(s)).expectData(3, 4, 14);
    }

    @Test
    public void minute() {
        NumExp<Integer> min = $offsetDateTime(0).minute();
        Series<OffsetDateTime> s = Series.of(
                OffsetDateTime.of(2007, 2, 8, 1, 12, 3, 0, ZoneOffset.ofHours(1)),
                OffsetDateTime.of(2008, 1, 1, 4, 10, 6, 0, ZoneOffset.ofHours(2)),
                OffsetDateTime.of(2009, 12, 8, 7, 59, 9, 0, ZoneOffset.ofHours(3)));
        new SeriesAsserts(min.eval(s)).expectData(12, 10, 59);
    }

    @Test
    public void second() {
        NumExp<Integer> exp = $offsetDateTime(0).second();
        Series<OffsetDateTime> s = Series.of(
                OffsetDateTime.of(2007, 2, 8, 1, 12, 11, 0, ZoneOffset.ofHours(1)),
                OffsetDateTime.of(2008, 1, 1, 4, 10, 1, 0, ZoneOffset.ofHours(2)),
                OffsetDateTime.of(2009, 12, 8, 7, 59, 59, 0, ZoneOffset.ofHours(3)));
        new SeriesAsserts(exp.eval(s)).expectData(11, 1, 59);
    }

    @Test
    public void millisecond() {
        NumExp<Integer> exp = $offsetDateTime(0).millisecond();
        Series<OffsetDateTime> s = Series.of(
                OffsetDateTime.of(2007, 2, 8, 1, 12, 11, 4_000_000, ZoneOffset.ofHours(1)),
                OffsetDateTime.of(2008, 1, 1, 4, 10, 1, 0, ZoneOffset.ofHours(2)),
                OffsetDateTime.of(2009, 12, 8, 7, 59, 59, 400_000, ZoneOffset.ofHours(3)));

        new SeriesAsserts(exp.eval(s)).expectData(4, 0, 0);
    }

    @Test
    public void eq() {
        Condition eq = $offsetDateTime("b").eq($col("c"));

        DataFrame df = DataFrame.foldByRow("b", "c").of(
                OffsetDateTime.of(2007, 2, 8, 1, 12, 11, 0, ZoneOffset.ofHours(1)), OffsetDateTime.of(2007, 2, 8, 1, 12, 11, 0, ZoneOffset.ofHours(1)),
                OffsetDateTime.of(2008, 1, 1, 4, 10, 1, 0, ZoneOffset.ofHours(2)), OffsetDateTime.of(2005, 1, 1, 4, 10, 1, 0, ZoneOffset.ofHours(2)),
                OffsetDateTime.of(2009, 12, 8, 7, 59, 59, 0, ZoneOffset.ofHours(4)), OffsetDateTime.of(2009, 12, 8, 7, 59, 59, 0, ZoneOffset.ofHours(3)));

        new BoolSeriesAsserts(eq.eval(df)).expectData(true, false, false);
    }

    @Test
    public void eqVal() {
        Condition eq = $offsetDateTime("b").eq(OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(2)));

        Series<OffsetDateTime> s = Series.of(
                OffsetDateTime.of(LocalDateTime.of(2007, 1, 8, 1, 1), ZoneOffset.UTC),
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(2)),
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(1)));

        new BoolSeriesAsserts(eq.eval(s)).expectData(false, true, false);
    }

    @Test
    public void eqStrVal() {
        Condition eq = $offsetDateTime("b").eq("2008-01-01T01:01+02");

        Series<OffsetDateTime> s = Series.of(
                OffsetDateTime.of(LocalDateTime.of(2007, 1, 8, 1, 1), ZoneOffset.UTC),
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(2)),
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(1)));

        new BoolSeriesAsserts(eq.eval(s)).expectData(false, true, false);
    }

    @Test
    public void ne() {
        Condition ne = $offsetDateTime("b").ne($col("c"));

        DataFrame df = DataFrame.foldByRow("b", "c").of(
                OffsetDateTime.of(2007, 2, 8, 1, 12, 11, 0, ZoneOffset.ofHours(1)), OffsetDateTime.of(2007, 2, 8, 1, 12, 11, 0, ZoneOffset.ofHours(1)),
                OffsetDateTime.of(2008, 1, 1, 4, 10, 1, 0, ZoneOffset.ofHours(2)), OffsetDateTime.of(2005, 1, 1, 4, 10, 1, 0, ZoneOffset.ofHours(2)),
                OffsetDateTime.of(2009, 12, 8, 7, 59, 59, 0, ZoneOffset.ofHours(4)), OffsetDateTime.of(2009, 12, 8, 7, 59, 59, 0, ZoneOffset.ofHours(3)));

        new BoolSeriesAsserts(ne.eval(df)).expectData(false, true, true);
    }

    @Test
    public void neVal() {
        Condition ne = $offsetDateTime("b").ne(OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(2)));

        Series<OffsetDateTime> s = Series.of(
                OffsetDateTime.of(LocalDateTime.of(2007, 1, 8, 1, 1), ZoneOffset.UTC),
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(2)),
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(1)));

        new BoolSeriesAsserts(ne.eval(s)).expectData(true, false, true);
    }

    @Test
    public void neStrVal() {
        Condition ne = $offsetDateTime("b").ne("2008-01-01T01:01+02");

        Series<OffsetDateTime> s = Series.of(
                OffsetDateTime.of(LocalDateTime.of(2007, 1, 8, 1, 1), ZoneOffset.UTC),
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(2)),
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(1)));

        new BoolSeriesAsserts(ne.eval(s)).expectData(true, false, true);
    }

    @Test
    public void lt() {
        Condition lt = $offsetDateTime("b").lt($col("c"));

        DataFrame df = DataFrame.foldByRow("b", "c").of(
                OffsetDateTime.of(LocalDateTime.of(2007, 1, 8, 1, 1, 1), ZoneOffset.ofHours(-1)), OffsetDateTime.of(LocalDateTime.of(2009, 1, 8, 1, 1, 1), ZoneOffset.UTC),
                OffsetDateTime.of(LocalDateTime.of(2009, 2, 2, 1, 1, 1), ZoneOffset.UTC), OffsetDateTime.of(LocalDateTime.of(2005, 3, 5, 1, 1, 1), ZoneOffset.UTC));

        new BoolSeriesAsserts(lt.eval(df)).expectData(true, false);
    }

    @Test
    public void ltVal() {
        Condition lt = $offsetDateTime(0).lt(
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(2)));

        Series<OffsetDateTime> s = Series.of(
                OffsetDateTime.of(LocalDateTime.of(2007, 1, 8, 1, 1), ZoneOffset.UTC),
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(2)),
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(1)));
        new BoolSeriesAsserts(lt.eval(s)).expectData(true, false, false);
    }

    @Test
    public void ltStrVal() {
        Condition lt = $offsetDateTime(0).lt("2008-01-01T01:01+02");

        Series<OffsetDateTime> s = Series.of(
                OffsetDateTime.of(LocalDateTime.of(2007, 1, 8, 1, 1), ZoneOffset.UTC),
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(2)),
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(1)));
        new BoolSeriesAsserts(lt.eval(s)).expectData(true, false, false);
    }

    @Test
    public void leVal() {
        Condition le = $offsetDateTime(0).le(
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(2)));

        Series<OffsetDateTime> s = Series.of(
                OffsetDateTime.of(LocalDateTime.of(2007, 1, 8, 1, 1), ZoneOffset.UTC),
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(2)),
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(1)));
        new BoolSeriesAsserts(le.eval(s)).expectData(true, true, false);
    }

    @Test
    public void leStrVal() {
        Condition le = $offsetDateTime(0).le("2008-01-01T01:01+02");
        Series<OffsetDateTime> s = Series.of(
                OffsetDateTime.of(LocalDateTime.of(2007, 1, 8, 1, 1), ZoneOffset.UTC),
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(2)),
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(1)));
        new BoolSeriesAsserts(le.eval(s)).expectData(true, true, false);
    }

    @Test
    public void betweenVal() {
        assertTrue(LocalDateTime.of(2012, 1, 1, 1, 2).isAfter(LocalDateTime.of(2012, 1, 1, 1, 1)));

        Condition le = $offsetDateTime(0).between(
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(2)),
                OffsetDateTime.of(LocalDateTime.of(2012, 1, 1, 1, 1), ZoneOffset.ofHours(2)));

        Series<OffsetDateTime> s = Series.of(
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 0), ZoneOffset.ofHours(2)),
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(2)),
                OffsetDateTime.of(LocalDateTime.of(2008, 5, 8, 5, 6), ZoneOffset.ofHours(2)),
                OffsetDateTime.of(LocalDateTime.of(2012, 1, 1, 1, 1), ZoneOffset.ofHours(2)),
                OffsetDateTime.of(LocalDateTime.of(2012, 1, 1, 1, 1), ZoneOffset.ofHours(-1)));

        new BoolSeriesAsserts(le.eval(s)).expectData(false, true, true, true, false);
    }

    @Test
    public void betweenStrVal() {
        assertTrue(LocalDateTime.of(2012, 1, 1, 1, 2).isAfter(LocalDateTime.of(2012, 1, 1, 1, 1)));

        Condition le = $offsetDateTime(0).between("2008-01-01T01:01+02", "2012-01-01T01:01+02");

        Series<OffsetDateTime> s = Series.of(
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 0), ZoneOffset.ofHours(2)),
                OffsetDateTime.of(LocalDateTime.of(2008, 1, 1, 1, 1), ZoneOffset.ofHours(2)),
                OffsetDateTime.of(LocalDateTime.of(2008, 5, 8, 5, 6), ZoneOffset.ofHours(2)),
                OffsetDateTime.of(LocalDateTime.of(2012, 1, 1, 1, 1), ZoneOffset.ofHours(2)),
                OffsetDateTime.of(LocalDateTime.of(2012, 1, 1, 1, 1), ZoneOffset.ofHours(-1)));

        new BoolSeriesAsserts(le.eval(s)).expectData(false, true, true, true, false);
    }

    @Test
    public void plusDays() {
        OffsetDateTimeExp exp = $offsetDateTime(0).plusDays(4);

        Series<OffsetDateTime> s = Series.of(
                OffsetDateTime.of(LocalDateTime.of(2007, 1, 8, 0, 1, 0), ZoneOffset.ofHours(1)),
                OffsetDateTime.of(LocalDateTime.of(2011, 12, 31, 1, 0, 1), ZoneOffset.UTC));

        new SeriesAsserts(exp.eval(s)).expectData(
                OffsetDateTime.of(LocalDateTime.of(2007, 1, 12, 0, 1, 0), ZoneOffset.ofHours(1)),
                OffsetDateTime.of(LocalDateTime.of(2012, 1, 4, 1, 0, 1), ZoneOffset.UTC));
    }

    @Test
    public void opsChain() {
        OffsetDateTimeExp exp = $offsetDateTime(0).plusDays(4).plusWeeks(1);

        Series<OffsetDateTime> s = Series.of(
                OffsetDateTime.of(LocalDateTime.of(2007, 1, 8, 1, 1, 1), ZoneOffset.ofHours(1)),
                OffsetDateTime.of(LocalDateTime.of(2011, 12, 31, 2, 2, 2), ZoneOffset.UTC));
        new SeriesAsserts(exp.eval(s)).expectData(
                OffsetDateTime.of(LocalDateTime.of(2007, 1, 19, 1, 1, 1), ZoneOffset.ofHours(1)),
                OffsetDateTime.of(LocalDateTime.of(2012, 1, 11, 2, 2, 2), ZoneOffset.UTC));
    }
}
