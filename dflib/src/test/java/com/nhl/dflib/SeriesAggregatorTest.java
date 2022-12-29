package com.nhl.dflib;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class SeriesAggregatorTest {

    @Test
    public void testAggregate_AverageDouble() {
        Series<Double> s = Series.of(1.4, 5.3, -9.4);
        assertEquals(-0.9, Exp.$double("").avg().eval(s).get(0).doubleValue(), 0.0000001);
    }

    @Test
    public void testAggregate_Concat() {
        Series<String> s = Series.of("a", "b", "z", "c");
        assertEquals("abzc", Exp.$col("").vConcat("").eval(s).get(0));
        assertEquals("[a|b|z|c]", Exp.$col("").vConcat("|", "[", "]").eval(s).get(0));
    }

    @Test
    public void testAggregate_First() {
        Series<String> s = Series.of("a", "b", "z", "c");
        assertEquals("a", Exp.$col("").first().eval(s).get(0));
    }

    @Test
    public void testAggregate_List() {
        Series<String> s = Series.of("a", "b", "z", "c");
        assertEquals(asList("a", "b", "z", "c"), Exp.$col("").list().eval(s).get(0));
    }

    @Test
    public void testAggregate_Max() {
        Series<Integer> s = Series.of(4, 5, -9);
        assertEquals(5, Exp.$int("").max().eval(s).get(0));
    }

    @Test
    public void testAggregate_Min() {
        Series<Integer> s = Series.of(4, 5, -9);
        assertEquals(-9, Exp.$int("").min().eval(s).get(0));
    }

    @Test
    public void testAggregate_MaxDouble() {
        Series<Double> s = Series.of(1.4, 5.3, -9.4);
        assertEquals(5.3, Exp.$double("").max().eval(s).get(0).doubleValue(), 0.0000001);
    }

    @Test
    public void testAggregate_MaxInt() {
        Series<Integer> s = Series.of(4, 5, -9);
        assertEquals(5, Exp.$int("").max().eval(s).get(0).intValue());
    }

    @Test
    public void testAggregate_MaxLong() {
        Series<Long> s = Series.of(4L, 5L, -9L);
        assertEquals(5L, Exp.$long("").max().eval(s).get(0).longValue());
    }

    @Test
    public void testAggregate_MedianDouble() {
        Series<Double> s = Series.of(1.4, 5.3, -9.4);
        assertEquals(1.4, Exp.$double("").median().eval(s).get(0).doubleValue(), 0.0000001);
    }

    @Test
    public void testAggregate_SumDouble() {
        Series<Double> s = Series.of(1.4, 5.3, -9.4);
        assertEquals(-2.7, Exp.$double("").sum().eval(s).get(0).doubleValue(), 0.0000001);
    }

    @Test
    public void testAggregate_SumBigDecimal() {
        Series<BigDecimal> s = Series.of(
                new BigDecimal("1.4").setScale(2, RoundingMode.HALF_UP),
                new BigDecimal("5.3").setScale(4, RoundingMode.HALF_UP),
                new BigDecimal("-9.4").setScale(2, RoundingMode.HALF_UP));

        assertEquals(BigDecimal.valueOf(-2.7000).setScale(4, RoundingMode.HALF_UP),
                Exp.$decimal("").sum().eval(s).get(0));
    }

    // TODO:  Exp.$decimal("").sum(2, RoundingMode.HALF_UP)
//    @Test
//    public void testAggregate_SumBigDecimal_Scale() {
//        Series<BigDecimal> s = Series.forData(
//                new BigDecimal("1.4").setScale(2, RoundingMode.HALF_UP),
//                new BigDecimal("5.3").setScale(4, RoundingMode.HALF_UP),
//                new BigDecimal("-9.4").setScale(2, RoundingMode.HALF_UP));
//
//        assertEquals(new BigDecimal("-2.7").setScale(2, RoundingMode.HALF_UP),
//                Exp.$decimal("").sum(2, RoundingMode.HALF_UP).eval(s).get(0));
//    }

    @Test
    public void testAggregate_Set() {
        Series<String> s = Series.of("a", "b", "z", "c");
        assertEquals(new HashSet<>(asList("a", "b", "z", "c")), Exp.$col("").set().eval(s).get(0));
    }
}
