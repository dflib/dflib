package org.dflib.exp.num;

import org.dflib.unit.BoolSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.DecimalExp;
import org.dflib.DoubleSeries;
import org.dflib.Exp;
import org.dflib.IntSeries;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.BaseExpTest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.stream.IntStream;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class IntColumnTest extends BaseExpTest {

    @Test
    public void getColumnName() {
        assertEquals("a", $int("a").getColumnName());
        assertEquals("$int(0)", $int(0).getColumnName());
    }

    @Test
    public void getColumnName_DataFrame() {
        DataFrame df = DataFrame.foldByRow("a", "b").of();
        assertEquals("b", $int("b").getColumnName(df));
        assertEquals("a", $int(0).getColumnName(df));
    }

    @Test
    public void chainStaysNumeric() {
        NumExp<?> exp = $int("b").as("x").as("y").sum().as("SUM(x)");
        assertEquals("SUM(x)", exp.getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void as() {
        NumExp<Integer> e = $int("b");
        assertEquals("b", e.getColumnName(mock(DataFrame.class)));
        assertEquals("c", e.as("c").getColumnName(mock(DataFrame.class)));
    }

    @Test
    public void eval() {
        NumExp<Integer> e = $int("b");

        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                "1", 2, 3,
                "4", Integer.MAX_VALUE, 6);

        new SeriesAsserts(e.eval(df)).expectData(2, Integer.MAX_VALUE);
    }

    @Test
    public void round() {
        DataFrame df = DataFrame.foldByRow("a").of(
                2,
                5,
                0,
                1,
                -2);

        Series<? extends Number> s = $int("a").round().eval(df);
        new SeriesAsserts(s).expectData(2, 5, 0, 1, -2);
    }

    @Test
    public void negate() {
        DataFrame df = DataFrame.foldByRow("a").of(1, 0, -3, Integer.MIN_VALUE, Integer.MAX_VALUE);

        Series<?> s = $int("a").negate().eval(df);
        new SeriesAsserts(s).expectData(-1, 0, 3, Integer.MIN_VALUE /* overflow */, -Integer.MAX_VALUE);
    }

    @Test
    public void castAsInt() {
        NumExp<Integer> e = $int("a");
        assertSame(e, e.castAsInt());
    }

    @Test
    public void castAsDecimal() {
        DecimalExp e = $int("a").castAsDecimal().scale(2);
        DataFrame df = DataFrame.foldByRow("a").of(
                2,
                355,
                -3);

        new SeriesAsserts(e.eval(df))
                .expectData(new BigDecimal("2.00"), new BigDecimal("355.00"), new BigDecimal("-3.00"));
    }

    @Test
    public void divide_Double() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                2., 3,
                3., 9);

        Series<? extends Number> s = $int("b").div($double("a")).eval(df);
        new SeriesAsserts(s).expectData(1.5, 3.);
    }

    @Test
    public void mod() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                2, 3,
                3, 9);

        Series<? extends Number> s = $int("b").mod($int("a")).eval(df);
        new SeriesAsserts(s).expectData(1, 0);
    }

    @Test
    public void mod_IntVal() {
        DataFrame df = DataFrame.foldByRow("a").of(
                5,
                0,
                11);

        Series<? extends Number> s = $int("a").mod(5).eval(df);
        new SeriesAsserts(s).expectData(0, 0, 1);
    }

    @Test
    public void abs() {
        DataFrame df = DataFrame.foldByRow("a").of(
                -5,
                0,
                11);

        Series<? extends Number> s = $int("a").abs().eval(df);
        new SeriesAsserts(s).expectData(5, 0, 11);
    }

    @Test
    public void add_Long() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1L, 2,
                3L, 4);

        Series<? extends Number> s = $int("b").add($long("a")).eval(df);
        new SeriesAsserts(s).expectData(3L, 7L);
    }

    @Test
    public void subtract_Int() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4);

        Series<? extends Number> s = $int("b").sub($int("a")).eval(df);
        assertFalse(s instanceof IntSeries);
        new SeriesAsserts(s).expectData(1, 1);
    }

    @Test
    public void multiply_Int() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                6, 2,
                3, 5);

        Series<? extends Number> s = $int("b").mul($int("a")).eval(df);
        assertFalse(s instanceof IntSeries);
        new SeriesAsserts(s).expectData(12, 15);
    }

    @Test
    public void multiply_Decimal() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                new BigDecimal("35.1"), 2,
                new BigDecimal("3.3"), 3);

        Series<? extends Number> s = $int("b").mul($decimal("a")).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("70.2"), new BigDecimal("9.9"));
    }


    @Test
    public void add_Int() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4);

        Series<? extends Number> s = $int("b").add($int("a")).eval(df);
        assertFalse(s instanceof IntSeries);
        new SeriesAsserts(s).expectData(3, 7);
    }

    @Test
    public void add_IntPrimitive() {
        DataFrame df = DataFrame.foldByRow("a", "b").ofStream(IntStream.of(1, 2, 3, 4));

        // sanity check of the test DataFrame
        Series<Integer> a = df.getColumn("a");
        assertTrue(a instanceof IntSeries);

        Series<Integer> b = df.getColumn("b");
        assertTrue(b instanceof IntSeries);

        // run and verify the calculation
        Series<? extends Number> s = $int("b").add($int("a")).eval(df);
        assertTrue(s instanceof IntSeries);
        new SeriesAsserts(s).expectData(3, 7);
    }

    @Test
    public void add_Double() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1.01, 2,
                3., 4);

        Series<? extends Number> s = $int("b").add($double("a")).eval(df);
        assertFalse(s instanceof DoubleSeries);
        new SeriesAsserts(s).expectData(3.01, 7.);
    }

    @Test
    public void th_Double() {
        Condition c = $int("b").lt($double("a"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1.01, -1,
                3., 4,
                3., 3);

        new BoolSeriesAsserts(c.eval(df)).expectData(true, false, false);
    }

    @Test
    public void gt_Int() {
        Condition c = $int("a").gt($int("b"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, -1,
                3, 3,
                3, 4);

        new BoolSeriesAsserts(c.eval(df)).expectData(true, false, false);
    }


    @Test
    public void ge_Int() {

        Condition c = $int("a").ge($int("b"));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, -1,
                3, 3,
                3, 4);

        new BoolSeriesAsserts(c.eval(df)).expectData(true, true, false);
    }

    @Test
    public void eq_IntVal() {

        Condition c = $int("a").eq(3);

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, -1,
                3, 3,
                3, 4);

        new BoolSeriesAsserts(c.eval(df)).expectData(false, true, true);
    }

    @Test
    public void eq_LongVal() {

        Condition c = $int("a").eq(3L);

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, -1,
                3, 3,
                3, 4);

        new BoolSeriesAsserts(c.eval(df)).expectData(false, true, true);
    }

    @Test
    public void eq_Decimal() {

        Condition c = $int("a").eq(new BigDecimal("3"));

        DataFrame df = DataFrame.foldByRow("a").of(
                1,
                3,
                3);

        new BoolSeriesAsserts(c.eval(df)).expectData(false, true, true);
    }

    @Test
    public void ne_Decimal() {

        Condition c = $int("a").ne(new BigDecimal("3"));

        DataFrame df = DataFrame.foldByRow("a").of(
                1,
                3,
                3);

        new BoolSeriesAsserts(c.eval(df)).expectData(true, false, false);
    }

    @Test
    public void between_IntPrimitive() {
        Condition c = $int("a").between($int("b"), $val(5));

        DataFrame df = DataFrame.foldByRow("a", "b").ofStream(IntStream.of(
                0, 1,
                1, 1,
                2, 1,
                5, 2,
                6, 2));

        // run and verify the calculation
        new BoolSeriesAsserts(c.eval(df)).expectData(false, true, true, true, false);
    }

    @Test
    public void notBetween_IntPrimitive() {
        Condition c = $int("a").notBetween($int("b"), $val(5));

        DataFrame df = DataFrame.foldByRow("a", "b").ofStream(IntStream.of(
                0, 1,
                1, 1,
                2, 1,
                5, 2,
                6, 2));

        // run and verify the calculation
        new BoolSeriesAsserts(c.eval(df)).expectData(true, false, false, false, true);
    }

    @Test
    public void map_Unary() {

        Exp<String> exp = $int("b").map(s -> s.map(i -> "_" + i));

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4);

        new SeriesAsserts(exp.eval(df)).expectData("_2", "_4");
    }

    @Test
    public void mapVal_Unary() {

        Exp<String> exp = $int("b").mapVal(i -> "_" + i);

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4,
                5, null);

        new SeriesAsserts(exp.eval(df)).expectData("_2", "_4", null);
    }

    @Test
    public void mapVal_Unary_WithNulls() {

        Exp<String> exp = $int("b").mapVal(i -> "_" + i, false);

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 4,
                5, null);

        new SeriesAsserts(exp.eval(df)).expectData("_2", "_4", "_null");
    }


    @Test
    public void map_Binary() {

        Exp<Boolean> exp = $int("b").map($int("a"), Series::eq);

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 3);

        new SeriesAsserts(exp.eval(df)).expectData(false, true);
    }

    @Test
    public void mapVal_Binary() {

        Exp<Boolean> exp = $int("b").mapVal($int("a"), Integer::equals);

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 2,
                3, 3,
                5, null);

        new SeriesAsserts(exp.eval(df)).expectData(false, true, null);
    }

    @Test
    public void cumSum() {
        NumExp<?> exp = $int("a").cumSum();

        DataFrame df = DataFrame.foldByRow("a").of(
                null,
                2,
                5,
                null,
                11,
                -12);

        new SeriesAsserts(exp.eval(df)).expectData(
                null,
                2L,
                7L,
                null,
                18L,
                6L
        );
    }

    @Test
    public void cumSum_getColumnName() {
        NumExp<?> exp = $int("a").cumSum();
        assertEquals("cumSum(a)", exp.getColumnName());
    }

    @Test
    public void sum_getColumnName() {
        NumExp<?> exp = $int("a").sum();
        assertEquals("sum(a)", exp.getColumnName());
    }

    @Test
    public void testEquals() {
        assertExpEquals(
                $int("a"),
                $int("a"),
                $int("b"));
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                $int("a"),
                $int("a"),
                $int("b"));
    }
}
