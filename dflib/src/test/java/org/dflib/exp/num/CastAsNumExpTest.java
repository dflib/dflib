package org.dflib.exp.num;

import org.dflib.BooleanSeries;
import org.dflib.DataFrame;
import org.dflib.IntSeries;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.unit.BoolSeriesAsserts;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicInteger;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.*;

public class CastAsNumExpTest {

    @Test
    public void add() {
        NumExp<?> exp = $col(0).castAsNumber().add(5);
        Series<Object> s = Series.of(new BigDecimal("5.01"), null, new BigDecimal("12"));
        new SeriesAsserts(exp.eval(s))
                .expectData(new BigDecimal("10.01"), null, new BigDecimal("17"));
    }

    @Test
    public void mul() {
        NumExp<?> exp = $col(0).castAsNumber().mul(2);
        Series<Object> s = Series.of(new BigDecimal("5.01"), null, new BigDecimal("12"));
        new SeriesAsserts(exp.eval(s))
                .expectData(new BigDecimal("10.02"), null, new BigDecimal("24"));
    }

    @Test
    public void add_Int() {
        NumExp<?> exp = $col(0).castAsNumber().add(5);
        Series<Object> s = Series.of(1, 2, 3);
        new SeriesAsserts(exp.eval(s)).expectData(6, 7, 8);
    }

    @Test
    public void add_IntSeries() {
        NumExp<?> exp = $col(0).castAsNumber().add(5);
        Series<? extends Number> result = exp.eval(Series.ofInt(1, 2, 3));

        assertInstanceOf(IntSeries.class, result);
        new SeriesAsserts(result).expectData(6, 7, 8);
    }

    @Test
    public void add_Long() {
        NumExp<?> exp = $col(0).castAsNumber().add(5);
        Series<Object> s = Series.of(1L, 2L, 3L);
        new SeriesAsserts(exp.eval(s)).expectData(6L, 7L, 8L);
    }

    @Test
    public void add_Mixed() {
        NumExp<?> exp = $col(0).castAsNumber().add(5);
        assertThrows(IllegalArgumentException.class, () -> exp.eval(Series.of(1, 2L, 3)));
        assertThrows(IllegalArgumentException.class, () -> exp.eval(Series.of(1, new BigDecimal("2.5"))));
    }

    @Test
    public void chain() {
        NumExp<?> exp = $col(0).castAsNumber().add(1).mul(2);
        Series<Object> s = Series.of(1, 2, 3);
        new SeriesAsserts(exp.eval(s)).expectData(4, 6, 8);
    }

    @Test
    public void chain_SplitEval() {
        Series<Object> s = Series.of(new BigDecimal("1"), new BigDecimal("2.5"), new BigDecimal("3"));

        Series<? extends Number> intermediate = $col(0).castAsNumber().add(1).eval(s);
        Series<? extends Number> result = $col(0).castAsNumber().mul(2).eval(intermediate);

        new SeriesAsserts(intermediate).expectData(new BigDecimal("2"), new BigDecimal("3.5"), new BigDecimal("4"));
        new SeriesAsserts(result).expectData(new BigDecimal("4"), new BigDecimal("7.0"), new BigDecimal("8"));
    }

    @Test
    public void sum_Int() {
        NumExp<?> exp = $col(0).castAsNumber().sum();
        Series<Object> s = Series.of(1, 2, 3);

        assertEquals(6L, exp.reduce(s));
        new SeriesAsserts(exp.eval(s)).expectData(6L, 6L, 6L);
    }

    @Test
    public void avg_Long() {
        NumExp<?> exp = $col(0).castAsNumber().avg();
        Series<Object> s = Series.of(1L, 2L, 3L);

        assertEquals(2.0, exp.reduce(s));
        new SeriesAsserts(exp.eval(s)).expectData(2.0, 2.0, 2.0);
    }

    @Test
    public void add_BigInteger() {
        NumExp<?> exp = $col(0).castAsNumber().add(BigInteger.ONE);
        Series<Object> s = Series.of(new BigInteger("9223372036854775808"));
        Series<? extends Number> result = exp.eval(s);

        new SeriesAsserts(result).expectData(new BigInteger("9223372036854775809"));
    }

    @Test
    public void filteredSum() {
        NumExp<?> exp = $col("a").castAsNumber().sum($bool("b"));
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, true,
                2, false,
                3, true);

        assertEquals(4L, exp.reduce(df));
        new SeriesAsserts(exp.eval(df)).expectData(4L, 4L, 4L);
    }

    @Test
    public void filteredSum_Empty() {
        NumExp<?> exp = $col("a").castAsNumber().sum($bool("b"));
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, false,
                2, false,
                3, false);

        assertEquals(BigDecimal.ZERO, exp.reduce(df));
        new SeriesAsserts(exp.eval(df)).expectData(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Test
    public void median() {
        NumExp<?> exp = $col("a").castAsNumber().median();
        DataFrame df = DataFrame.foldByRow("a").of(
                "10.0",
                "1000.0",
                "20.0",
                "30.0");

        assertEquals(new BigDecimal("25.00"), exp.reduce(df));
    }

    @Test
    public void lt_Dynamic() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 1L,
                2, 3L,
                3, 2L,
                null, 1L);

        new BoolSeriesAsserts($col("a").castAsNumber().lt($col("b").castAsNumber()).eval(df))
                .expectData(false, true, false, false);
    }

    @Test
    public void lt_Strings() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                "1.5", 2,
                "2.5", 2);

        new BoolSeriesAsserts($col("a").castAsNumber().lt($col("b").castAsNumber()).eval(df))
                .expectData(true, false);
    }

    @Test
    public void between_Dynamic() {
        DataFrame df = DataFrame.foldByRow("a", "f", "t").of(
                "2.5", 2, 3.0,
                "3", 3, 4.0,
                "5.0", 4, 5.0,
                "7", 8, 9.0,
                null, 1, 2.0);

        BooleanSeries isBetween = $col("a").castAsNumber().between(
                $col("f").castAsNumber(),
                $col("t").castAsNumber()
        ).eval(df);
        new BoolSeriesAsserts(isBetween)
                .expectData(true, true, true, false, false);
    }

    @Test
    public void abs_DataFrame_EvalOnce() {
        AtomicInteger counter = new AtomicInteger();

        NumExp<?> exp = $col("a")
                .mapVal(v -> {
                    counter.incrementAndGet();
                    return v;
                })
                .castAsNumber()
                .abs();
        DataFrame df = DataFrame.foldByRow("a").of(new BigDecimal("-1"), new BigDecimal("-2"), new BigDecimal("-3.5"));

        new SeriesAsserts(exp.eval(df))
                .expectData(new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3.5"));
        assertEquals(3, counter.get());
    }

    @Test
    public void add_DataFrame_EvalOnce() {
        AtomicInteger leftCounter = new AtomicInteger();
        AtomicInteger rightCounter = new AtomicInteger();

        NumExp<?> exp = $col("a")
                .mapVal(v -> {
                    leftCounter.incrementAndGet();
                    return v;
                })
                .castAsNumber()
                .add($col("b")
                        .mapVal(v -> {
                            rightCounter.incrementAndGet();
                            return v;
                        })
                        .castAsNumber());
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, new BigDecimal("1"),
                2, new BigDecimal("2.5"),
                3, new BigDecimal("3.5"));

        new SeriesAsserts(exp.eval(df))
                .expectData(new BigDecimal("2"), new BigDecimal("4.5"), new BigDecimal("6.5"));
        assertEquals(3, leftCounter.get());
        assertEquals(3, rightCounter.get());
    }

    @Test
    public void shift_Add() {
        NumExp<?> exp = $col(0).castAsNumber().shift(1).add(1);
        Series<Object> s = Series.of(1, 2, 3);
        Series<? extends Number> result = exp.eval(s);

        new SeriesAsserts(result).expectData(null, 2, 3);
    }

    @Test
    public void castAsDecimal_Int() {
        Series<BigDecimal> result = $col(0)
                .castAsNumber()
                .castAsDecimal()
                .scale(2)
                .eval(Series.of(1, 2, 3));

        new SeriesAsserts(result).expectData(new BigDecimal("1.00"), new BigDecimal("2.00"), new BigDecimal("3.00"));
    }

    @Test
    public void castAsDecimal_Long() {
        Series<BigDecimal> result = $col(0)
                .castAsNumber()
                .castAsDecimal()
                .eval(Series.of(Long.MAX_VALUE));

        // must not lose precision going through "double"
        new SeriesAsserts(result).expectData(BigDecimal.valueOf(Long.MAX_VALUE));
    }

    @Test
    public void castAsDecimal_BigDecimal() {
        Series<BigDecimal> result = $col(0)
                .castAsNumber()
                .castAsDecimal()
                .scale(2)
                .eval(Series.of(new BigDecimal("1.234"), new BigDecimal("4.5")));

        new SeriesAsserts(result).expectData(new BigDecimal("1.23"), new BigDecimal("4.50"));
    }

    @Test
    public void castAsDecimal_Nulls() {
        Series<BigDecimal> result = $col(0)
                .castAsNumber()
                .castAsDecimal()
                .scale(2)
                .eval(Series.of(1.5d, null, 2.5d, null, 3.75d));

        new SeriesAsserts(result).expectData(
                new BigDecimal("1.50"),
                null,
                new BigDecimal("2.50"),
                null,
                new BigDecimal("3.75"));
    }

    @Test
    public void castAsInt_Strings() {
        Series<Integer> result = $col(0)
                .castAsNumber()
                .castAsInt()
                .eval(Series.of("1", null, "2.7"));

        new SeriesAsserts(result).expectData(1, null, 2);
    }

    @Test
    public void castAsLong_Mixed() {
        NumExp<Long> exp = $col(0).castAsNumber().castAsLong();
        assertThrows(IllegalArgumentException.class, () -> exp.eval(Series.of(1, 2L)));
    }

    @Test
    public void homogeneous() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(1, null, 2, 3);
        new SeriesAsserts(exp.eval(s))
                .expectData(1, null, 2, 3);
    }

    @Test
    public void parseableString() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of("1.25", "1", null, "2");

        new SeriesAsserts(exp.eval(s))
                .expectData(new BigDecimal("1.25"), new BigDecimal("1"), null, new BigDecimal("2"));
    }

    @Test
    public void parseableStringsAndNumbers() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of("1.25", 1, 2L);

        new SeriesAsserts(exp.eval(s))
                .expectData(new BigDecimal("1.25"), new BigDecimal("1"), new BigDecimal("2"));
    }

    @Test
    public void parseableObject() {
        NumExp<?> exp = $col(0).castAsNumber();
        Object value = new Object() {
            @Override
            public String toString() {
                return "3.75";
            }
        };

        new SeriesAsserts(exp.eval(Series.of(value, value)))
                .expectData(new BigDecimal("3.75"), new BigDecimal("3.75"));
    }

    @Test
    public void unparseableString() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of("abc", "1", "2");
        assertThrows(NumberFormatException.class, () -> exp.eval(s).materialize());
    }

    @Test
    public void homogeneous_Long() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(1L, null, 2L, 3L);
        Series<? extends Number> result = exp.eval(s);
        new SeriesAsserts(result).expectData(1L, null, 2L, 3L);
    }

    @Test
    public void homogeneous_Double() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(1.0, null, 2.0);
        Series<? extends Number> result = exp.eval(s);
        new SeriesAsserts(result).expectData(1.0, null, 2.0);
    }

    @Test
    public void homogeneous_Float() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(1.0f, null, 2.0f);
        Series<? extends Number> result = exp.eval(s);
        new SeriesAsserts(result).expectData(1.0f, null, 2.0f);
    }

    @Test
    public void homogeneous_BigInteger() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(BigInteger.ONE, null, BigInteger.TEN);
        Series<? extends Number> result = exp.eval(s);
        new SeriesAsserts(result).expectData(BigInteger.ONE, null, BigInteger.TEN);
    }

    @Test
    public void homogeneous_BigDecimal() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(new BigDecimal("1.5"), null);
        Series<? extends Number> result = exp.eval(s);
        new SeriesAsserts(result).expectData(new BigDecimal("1.5"), null);
    }

    @Test
    public void homogeneous_Short() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of((short) 1, (short) 2);
        Series<? extends Number> result = exp.eval(s);
        new SeriesAsserts(result).expectData(BigDecimal.valueOf(1), BigDecimal.valueOf(2));
    }

    @Test
    public void homogeneous_Byte() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of((byte) 1, (byte) 2);
        Series<? extends Number> result = exp.eval(s);
        new SeriesAsserts(result).expectData(BigDecimal.valueOf(1), BigDecimal.valueOf(2));
    }

    @Test
    public void mixed() {
        NumExp<?> exp = $col(0).castAsNumber();
        assertThrows(IllegalArgumentException.class, () -> exp.eval(Series.of(1, 2L)));
        assertThrows(IllegalArgumentException.class, () -> exp.eval(Series.of(1.5f, 2.0)));
        assertThrows(IllegalArgumentException.class, () -> exp.eval(Series.of(BigInteger.ONE, new BigDecimal("2.5"))));
    }

    @Test
    public void emptySeries() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of();
        Series<? extends Number> result = exp.eval(s);
        new SeriesAsserts(result).expectData();
    }

    @Test
    public void allNulls() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(null, null, null);
        Series<? extends Number> result = exp.eval(s);
        new SeriesAsserts(result).expectData(null, null, null);
    }

    @Test
    public void unknownNumberSubtype() {
        Number custom = new Number() {
            @Override
            public int intValue() {
                return 42;
            }

            @Override
            public long longValue() {
                return 42L;
            }

            @Override
            public float floatValue() {
                return 42.0f;
            }

            @Override
            public double doubleValue() {
                return 42.0;
            }
        };

        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(custom, null, custom);
        Series<? extends Number> result = exp.eval(s);

        // a Number type without a factory is converted to BigDecimal
        new SeriesAsserts(result).expectData(new BigDecimal("42"), null, new BigDecimal("42"));
    }

    @Test
    public void eval_DataFrame() {
        NumExp<?> exp = $col("a").castAsNumber();
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                "1", "x",
                "2", "y",
                "3.5", "z");
        Series<? extends Number> result = exp.eval(df);
        new SeriesAsserts(result).expectData(new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3.5"));
    }

    @Test
    public void reduce_Series_Number() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(42, 1, 2);
        assertEquals(42, exp.reduce(s));
    }

    @Test
    public void reduce_Series_Null() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(null, 1, 2);
        assertNull(exp.reduce(s));
    }

    @Test
    public void reduce_Series_ParseableString() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of("42.5", "1", "2");
        assertEquals(new BigDecimal("42.5"), exp.reduce(s));
    }

    @Test
    public void reduce_Series_UnparseableString() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of("abc", "1", "2");
        assertThrows(NumberFormatException.class, () -> exp.reduce(s));
    }

    @Test
    public void reduce_DataFrame() {
        NumExp<?> exp = $col("a").castAsNumber();
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                99, "x",
                2, "y");
        assertEquals(99, exp.reduce(df));
    }

    @Test
    public void abs_Reduce() {
        AtomicInteger counter = new AtomicInteger();

        NumExp<?> exp = $col(0)
                .mapVal(v -> counter.incrementAndGet())
                .castAsNumber()
                .abs();
        Series<Object> s = Series.of("x", "y");

        assertEquals(1, exp.reduce(s));
        assertEquals(1, counter.get());
    }

    @Test
    public void alreadyNumeric() {
        NumExp<?> exp = $int(0);
        assertSame(exp, exp.castAsNumber());
    }

    @Test
    public void toQL() {
        String ql = $col("a").castAsNumber().toQL();
        assertEquals("castAsNumber(a)", ql);
    }
}
