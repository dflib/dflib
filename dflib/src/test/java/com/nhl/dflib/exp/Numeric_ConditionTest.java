package com.nhl.dflib.exp;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.LongSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.unit.BooleanSeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.stream.LongStream;

import static com.nhl.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Numeric_ConditionTest {

    @Test
    public void testLT_IntDouble() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1.01, -1,
                3., 4,
                3., 3);

        BooleanSeries s = $int("b").lt($double("a")).eval(df);
        new BooleanSeriesAsserts(s).expectData(true, false, false);
    }

    @Test
    public void testLE_LongInt() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1L, -1,
                3L, 3,
                3L, 4);

        BooleanSeries s = $long("a").le($int("b")).eval(df);
        new BooleanSeriesAsserts(s).expectData(false, true, true);
    }

    @Test
    public void testGT_IntInt() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, -1,
                3, 3,
                3, 4);

        BooleanSeries s = $int("a").gt($int("b")).eval(df);
        new BooleanSeriesAsserts(s).expectData(true, false, false);
    }

    @Test
    public void testGE_IntInt() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, -1,
                3, 3,
                3, 4);

        BooleanSeries s = $int("a").ge($int("b")).eval(df);
        new BooleanSeriesAsserts(s).expectData(true, true, false);
    }

    @Test
    public void testLT_LongLong_Primitive() {
        DataFrame df = DataFrame.newFrame("a", "b").foldLongStreamByRow(LongStream.of(2L, 1L, 3L, 4L));
        // sanity check of the test DataFrame
        Series<Long> a = df.getColumn("a");
        assertTrue(a instanceof LongSeries);

        Series<Long> b = df.getColumn("b");
        assertTrue(b instanceof LongSeries);

        // run and verify the calculation
        BooleanSeries s = $long("a").lt($long("b")).eval(df);
        new BooleanSeriesAsserts(s).expectData(false, true);
    }

    @Test
    public void testGT_DecimalDecimal() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                new BigDecimal("1.1"), new BigDecimal("1.0001"),
                new BigDecimal("3"), new BigDecimal("3"),
                new BigDecimal("1.1"), new BigDecimal("1.2"));

        BooleanSeries s = $decimal("a").gt($decimal("b")).eval(df);
        new BooleanSeriesAsserts(s).expectData(true, false, false);
    }
}
