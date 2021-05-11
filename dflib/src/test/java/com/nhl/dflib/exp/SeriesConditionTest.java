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

public class SeriesConditionTest {

    @Test
    public void testIsNull() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                "1",
                "4",
                null,
                "5");

        BooleanSeries s = $col("a").isNull().eval(df);
        new BooleanSeriesAsserts(s).expectData(false, false, true, false);
    }

    @Test
    public void testIsNotNull() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                "1",
                "4",
                null,
                "5");

        BooleanSeries s = $col("a").isNotNull().eval(df);
        new BooleanSeriesAsserts(s).expectData(true, true, false, true);
    }

    @Test
    public void testEq() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                "1", "1",
                "4", "5");

        BooleanSeries s = $col("b").eq($col("a")).eval(df);
        new BooleanSeriesAsserts(s).expectData(true, false);
    }

    @Test
    public void testNe() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                "1", "1",
                "4", "5");

        BooleanSeries s = $col("b").ne($col("a")).eval(df);
        new BooleanSeriesAsserts(s).expectData(false, true);
    }

    @Test
    public void testAnd() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                "1", "1", false,
                "2", "2", true,
                "4", "5", true);

        BooleanSeries s = $col("b").eq($col("a")).and($bool("c")).eval(df);
        new BooleanSeriesAsserts(s).expectData(false, true, false);
    }

    @Test
    public void testOr() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                "1", "1", false,
                "2", "2", true,
                "4", "5", false);

        BooleanSeries s = $col("b").eq($col("a")).or($bool("c")).eval(df);
        new BooleanSeriesAsserts(s).expectData(true, true, false);
    }

    @Test
    public void testOr_Multiple() {
        DataFrame df = DataFrame.newFrame("a", "b", "c").foldByRow(
                false, false, false,
                true, true, true,
                true, false, false);

        BooleanSeries s = or($bool("a"), $bool("b"), $bool("c")).eval(df);
        new BooleanSeriesAsserts(s).expectData(false, true, true);
    }

    @Test
    public void testIntLtDouble() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1.01, -1,
                3., 4,
                3., 3);

        BooleanSeries s = $int("b").lt($double("a")).eval(df);
        new BooleanSeriesAsserts(s).expectData(true, false, false);
    }

    @Test
    public void testLongLeInt() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1L, -1,
                3L, 3,
                3L, 4);

        BooleanSeries s = $long("a").le($int("b")).eval(df);
        new BooleanSeriesAsserts(s).expectData(false, true, true);
    }

    @Test
    public void testIntGtInt() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, -1,
                3, 3,
                3, 4);

        BooleanSeries s = $int("a").gt($int("b")).eval(df);
        new BooleanSeriesAsserts(s).expectData(true, false, false);
    }

    @Test
    public void testIntGeInt() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, -1,
                3, 3,
                3, 4);

        BooleanSeries s = $int("a").ge($int("b")).eval(df);
        new BooleanSeriesAsserts(s).expectData(true, true, false);
    }

    @Test
    public void testLongLtLong_Primitive() {
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
    public void testComposite() {
        DataFrame df = DataFrame.newFrame("a", "b", "c", "d").foldByRow(
                1.01, -1, 0, 1,
                60., 4, 8, 2);

        BooleanSeries s = $int("b").multiply($int("c")).lt($double("a").divide($int("d"))).eval(df);
        new BooleanSeriesAsserts(s).expectData(true, false);
    }

    @Test
    public void testDecimalGtDecimal() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                new BigDecimal("1.1"), new BigDecimal("1.0001"),
                new BigDecimal("3"), new BigDecimal("3"),
                new BigDecimal("1.1"), new BigDecimal("1.2"));

        BooleanSeries s = $decimal("a").gt($decimal("b")).eval(df);
        new BooleanSeriesAsserts(s).expectData(true, false, false);
    }
}