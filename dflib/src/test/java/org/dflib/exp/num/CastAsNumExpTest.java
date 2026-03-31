package org.dflib.exp.num;

import org.dflib.BooleanSeries;
import org.dflib.DataFrame;
import org.dflib.DoubleSeries;
import org.dflib.IntSeries;
import org.dflib.LongSeries;
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
        Series<Object> s = Series.of(new BigDecimal("5.01"), null, 12L, 1);
        new SeriesAsserts(exp.eval(s))
                .expectData(new BigDecimal("10.01"), null, new BigDecimal("17"), new BigDecimal("6"));
    }

    @Test
    public void mul() {
        NumExp<?> exp = $col(0).castAsNumber().mul(2);
        Series<Object> s = Series.of(new BigDecimal("5.01"), null, 12L, 1);
        new SeriesAsserts(exp.eval(s))
                .expectData(new BigDecimal("10.02"), null, new BigDecimal("24"), new BigDecimal("2"));
    }

    @Test
    public void add_int() {
        NumExp<?> exp = $col(0).castAsNumber().add(5);
        Series<Object> s = Series.of(1, 2, 3);
        Series<? extends Number> result = exp.eval(s);

        assertInstanceOf(IntSeries.class, result);
        new SeriesAsserts(result).expectData(6, 7, 8);
    }

    @Test
    public void add_long() {
        NumExp<?> exp = $col(0).castAsNumber().add(5);
        Series<Object> s = Series.of(1L, 2L, 3L);
        Series<? extends Number> result = exp.eval(s);

        assertInstanceOf(LongSeries.class, result);
        new SeriesAsserts(result).expectData(6L, 7L, 8L);
    }

    @Test
    public void add_intLong() {
        NumExp<?> exp = $col(0).castAsNumber().add(5);
        Series<Object> s = Series.of(1, 2L, 3);
        Series<? extends Number> result = exp.eval(s);

        assertInstanceOf(LongSeries.class, result);
        new SeriesAsserts(result).expectData(6L, 7L, 8L);
    }

    @Test
    public void chain() {
        NumExp<?> exp = $col(0).castAsNumber().add(1).mul(2);
        Series<Object> s = Series.of(1, 2, 3);
        Series<? extends Number> result = exp.eval(s);

        assertInstanceOf(IntSeries.class, result);
        new SeriesAsserts(result).expectData(4, 6, 8);
    }

    @Test
    public void chain_splitEval() {
        Series<Object> s = Series.of(1, new BigDecimal("2.5"), 3L);

        Series<? extends Number> intermediate = $col(0).castAsNumber().add(1).eval(s);
        Series<? extends Number> result = $col(0).castAsNumber().mul(2).eval(intermediate);

        new SeriesAsserts(intermediate).expectData(new BigDecimal("2"), new BigDecimal("3.5"), new BigDecimal("4"));
        new SeriesAsserts(result).expectData(new BigDecimal("4"), new BigDecimal("7.0"), new BigDecimal("8"));
    }

    @Test
    public void sum_int() {
        NumExp<?> exp = $col(0).castAsNumber().sum();
        Series<Object> s = Series.of(1, 2, 3);

        assertEquals(6L, exp.reduce(s));
        assertInstanceOf(LongSeries.class, exp.eval(s));
    }

    @Test
    public void avg_long() {
        NumExp<?> exp = $col(0).castAsNumber().avg();
        Series<Object> s = Series.of(1L, 2L, 3L);
        Series<? extends Number> result = exp.eval(s);

        assertEquals(2.0, exp.reduce(s));
        assertInstanceOf(DoubleSeries.class, result);
        new SeriesAsserts(result).expectData(2.0, 2.0, 2.0);
    }

    @Test
    public void add_bigInteger() {
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
        assertInstanceOf(LongSeries.class, exp.eval(df));
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
    public void lt_dynamic() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, 1L,
                "2.5", 2.5d,
                3L, new BigDecimal("4"),
                null, 1);

        new BoolSeriesAsserts($col("a").castAsNumber().lt($col("b").castAsNumber()).eval(df))
                .expectData(false, false, true, false);
    }

    @Test
    public void between_dynamic() {
        DataFrame df = DataFrame.foldByRow("a", "f", "t").of(
                "2.5", 2, 3,
                3L, 3, 4.0,
                new BigDecimal("5.0"), 4L, new BigDecimal("5.0"),
                7, 8, 9,
                null, 1, 2);

        BooleanSeries isBetween = $col("a").castAsNumber().between(
                $col("f").castAsNumber(),
                $col("t").castAsNumber()
        ).eval(df);
        new BoolSeriesAsserts(isBetween)
                .expectData(true, true, true, false, false);
    }

    @Test
    public void shift_thenAdd() {
        NumExp<?> exp = $col(0).castAsNumber().shift(1).add(1);
        Series<Object> s = Series.of(1, 2, 3);
        Series<? extends Number> result = exp.eval(s);

        new SeriesAsserts(result).expectData(null, 2, 3);
    }

    @Test
    public void castAsDecimal_int() {
        Series<BigDecimal> result = $col(0)
                .castAsNumber()
                .castAsDecimal()
                .scale(2)
                .eval(Series.of(1, 2, 3));

        new SeriesAsserts(result).expectData(new BigDecimal("1.00"), new BigDecimal("2.00"), new BigDecimal("3.00"));
    }

    @Test
    public void castAsDecimal_bigDecimal() {
        Series<BigDecimal> result = $col(0)
                .castAsNumber()
                .castAsDecimal()
                .scale(2)
                .eval(Series.of(new BigDecimal("1.234"), new BigDecimal("4.5")));

        new SeriesAsserts(result).expectData(new BigDecimal("1.23"), new BigDecimal("4.50"));
    }

    @Test
    public void castAsDecimal_nulls() {
        Series<BigDecimal> result = $col(0)
                .castAsNumber()
                .castAsDecimal()
                .scale(2)
                .eval(Series.of(1, null, new BigDecimal("2.5"), null, 3.75d));

        new SeriesAsserts(result).expectData(
                new BigDecimal("1.00"),
                null,
                new BigDecimal("2.50"),
                null,
                new BigDecimal("3.75"));
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

        new SeriesAsserts(exp.eval(Series.of(value, 1)))
                .expectData(new BigDecimal("3.75"), new BigDecimal("1"));
    }

    @Test
    public void unparseableString() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of("abc", 1, 2);
        assertThrows(NumberFormatException.class, () -> exp.eval(s));
    }

    // --- Homogeneous type inference ---

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

    // --- Pairwise widening ---

    @Test
    public void widen_Int_Long() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(1, 2L);
        Series<? extends Number> result = exp.eval(s);
        new SeriesAsserts(result).expectData(1L, 2L);
    }

    @Test
    public void widen_Int_Double() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(1, 2.0);
        Series<? extends Number> result = exp.eval(s);
        new SeriesAsserts(result).expectData(1.0, 2.0);
    }

    @Test
    public void widen_Int_Float() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(1, 2.0f);
        Series<? extends Number> result = exp.eval(s);
        new SeriesAsserts(result).expectData(1.0f, 2.0f);
    }

    @Test
    public void widen_Int_BigInteger() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(1, BigInteger.TEN);
        Series<? extends Number> result = exp.eval(s);
        new SeriesAsserts(result).expectData(BigInteger.valueOf(1), BigInteger.TEN);
    }

    @Test
    public void widen_Long_Double() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(1L, 2.0);
        Series<? extends Number> result = exp.eval(s);
        new SeriesAsserts(result).expectData(1.0, 2.0);
    }

    @Test
    public void widen_Long_BigInteger() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(1L, BigInteger.TEN);
        Series<? extends Number> result = exp.eval(s);
        new SeriesAsserts(result).expectData(BigInteger.valueOf(1), BigInteger.TEN);
    }

    @Test
    public void widen_Float_Double() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(1.0f, 2.0);
        Series<? extends Number> result = exp.eval(s);
        new SeriesAsserts(result).expectData(1.0, 2.0);
    }

    @Test
    public void widen_BigInteger_BigDecimal() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(BigInteger.ONE, new BigDecimal("2.5"));
        Series<? extends Number> result = exp.eval(s);
        new SeriesAsserts(result).expectData(new BigDecimal(BigInteger.ONE), new BigDecimal("2.5"));
    }

    // --- Edge cases ---

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
        // Custom Number subclass not in typeConversionRank → falls back to BigDecimal via doubleValue()
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
        Series<Object> s = Series.of(custom, 10);
        Series<? extends Number> result = exp.eval(s);

        // unknown subtype forces BigDecimal; both values converted via BigDecimal path
        new SeriesAsserts(result).expectData(new BigDecimal("42"), BigDecimal.valueOf(10));
    }

    // --- DataFrame eval path ---

    @Test
    public void eval_DataFrame() {
        NumExp<?> exp = $col("a").castAsNumber();
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2L, "y",
                new BigDecimal("3.5"), "z");
        Series<? extends Number> result = exp.eval(df);
        new SeriesAsserts(result).expectData(new BigDecimal("1"), new BigDecimal("2"), new BigDecimal("3.5"));
    }

    // --- reduce() methods ---

    @Test
    public void reduce_Series_number() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(42, 1, 2);
        assertEquals(42, exp.reduce(s));
    }

    @Test
    public void reduce_Series_null() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(null, 1, 2);
        assertNull(exp.reduce(s));
    }

    @Test
    public void reduce_Series_parseableString() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of("42.5", 1, 2);
        assertEquals(new BigDecimal("42.5"), exp.reduce(s));
    }

    @Test
    public void reduce_Series_unparseableString() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of("abc", 1, 2);
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
    public void abs_reduce() {
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
    public void add_reduce_widening() {
        NumExp<?> exp = $col(0).castAsNumber().add(1);
        Series<Object> s = Series.of(Long.MAX_VALUE, BigInteger.ONE);

        Number firstEval = exp.eval(s).first();

        assertEquals(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE), firstEval);

        // NOTE: numeric overflow is expected outcome in this case, series is evaluated lazily
        //       so we do not infer BigInteger type from the source
        assertEquals(Long.MIN_VALUE, exp.reduce(s));
    }

    // --- Converter specifics ---

    @Test
    public void bigDecimal_fromBigInteger() {
        NumExp<?> exp = $col(0).castAsNumber();
        Series<Object> s = Series.of(BigInteger.valueOf(123), new BigDecimal("4.5"));
        Series<? extends Number> result = exp.eval(s);
        // BigInteger→BigDecimal uses new BigDecimal(BigInteger)
        new SeriesAsserts(result).expectData(new BigDecimal(BigInteger.valueOf(123)), new BigDecimal("4.5"));
    }

    @Test
    public void bigDecimal_fromDouble() {
        NumExp<?> exp = $col(0).castAsNumber();
        // Mix Double and BigDecimal to trigger BigDecimal target with Double conversion
        Series<Object> s = Series.of(1.5, new BigDecimal("2.5"));
        Series<? extends Number> result = exp.eval(s);
        // Double→BigDecimal uses BigDecimal.valueOf(doubleValue()).stripTrailingZeros()
        new SeriesAsserts(result).expectData(new BigDecimal("1.5"), new BigDecimal("2.5"));
    }

    @Test
    public void bigDecimal_fromLong() {
        NumExp<?> exp = $col(0).castAsNumber();
        // Mix Long and BigDecimal to trigger BigDecimal target with Long→longValue() conversion
        Series<Object> s = Series.of(100L, new BigDecimal("2.5"));
        Series<? extends Number> result = exp.eval(s);
        // Long→BigDecimal uses BigDecimal.valueOf(longValue()), not stripTrailingZeros()
        new SeriesAsserts(result).expectData(new BigDecimal("100"), new BigDecimal("2.5"));
    }

    // --- Short/Byte fallback to BigDecimal ---

    @Test
    public void widen_Short_toBigDecimal() {
        NumExp<?> exp = $col(0).castAsNumber();
        // Short is not in typeConversionRank → triggers BigDecimal fallback
        // Short is converted via longValue() path, not doubleValue()
        Series<Object> s = Series.of((short) 1, 2);
        Series<? extends Number> result = exp.eval(s);
        new SeriesAsserts(result).expectData(BigDecimal.valueOf(1), BigDecimal.valueOf(2));
    }

    @Test
    public void widen_Byte_toBigDecimal() {
        NumExp<?> exp = $col(0).castAsNumber();
        // Byte is not in typeConversionRank → triggers BigDecimal fallback
        // Byte is converted via longValue() path, not doubleValue()
        Series<Object> s = Series.of((byte) 1, 2);
        Series<? extends Number> result = exp.eval(s);
        new SeriesAsserts(result).expectData(BigDecimal.valueOf(1), BigDecimal.valueOf(2));
    }

    // --- NumExp identity override ---

    @Test
    public void alreadyNumeric() {
        NumExp<?> exp = $int(0);
        assertSame(exp, exp.castAsNumber());
    }

    // --- toQL serialization ---

    @Test
    public void toQL() {
        String ql = $col("a").castAsNumber().toQL();
        assertEquals("castAsNumber(a)", ql);
    }
}
