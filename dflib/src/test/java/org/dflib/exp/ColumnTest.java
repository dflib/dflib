package org.dflib.exp;

import org.dflib.BooleanSeries;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.StrExp;
import org.dflib.unit.BoolSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.dflib.Exp.$col;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class ColumnTest {

    @Test
    public void castAsStr() {
        StrExp str = $col(0).castAsStr();
        Series<Object> s = Series.of("a", null, LocalDate.of(2021, 1, 2));
        new SeriesAsserts(str.eval(s)).expectData("a", null, "2021-01-02");
    }

    @Test
    public void castAsInt() {
        NumExp<Integer> exp = $col(0).castAsInt();
        Series<Object> s = Series.of(new BigDecimal("5.01"), null, 12L);
        new SeriesAsserts(exp.eval(s)).expectData(5, null, 12);
    }

    @Test
    public void castAsLong() {
        NumExp<Long> exp = $col(0).castAsLong();
        Series<Object> s = Series.of(new BigDecimal("5.01"), null, 12L, 1);
        new SeriesAsserts(exp.eval(s)).expectData(5L, null, 12L, 1L);
    }

    @Test
    public void castAsDouble() {
        NumExp<Double> exp = $col(0).castAsDouble();
        Series<Object> s = Series.of(new BigDecimal("5.01"), null, 12L, 1);
        new SeriesAsserts(exp.eval(s)).expectData(5.01, null, 12., 1.);
    }

    @Test
    public void getColumnName() {
        assertEquals("a", $col("a").getColumnName());
        assertEquals("$col(0)", $col(0).getColumnName());
    }

    @Test
    public void name_DataFrame() {
        DataFrame df = DataFrame.foldByRow("a", "b").of();
        assertEquals("b", $col("b").getColumnName(df));
        assertEquals("a", $col(0).getColumnName(df));
    }

    @Test
    public void as() {
        Exp<?> e = $col("b");
        assertEquals("b", e.getColumnName(mock(DataFrame.class)));
        assertEquals("c", e.as("c").getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void isNull() {
        DataFrame df = DataFrame.foldByRow("a").of(
                "1",
                "4",
                null,
                "5");

        BooleanSeries s = $col("a").isNull().eval(df);
        new BoolSeriesAsserts(s).expectData(false, false, true, false);
    }

    @Test
    public void isNotNull() {
        DataFrame df = DataFrame.foldByRow("a").of(
                "1",
                "4",
                null,
                "5");

        BooleanSeries s = $col("a").isNotNull().eval(df);
        new BoolSeriesAsserts(s).expectData(true, true, false, true);
    }
}
