package org.dflib.exp.num;

import org.dflib.Series;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class NumberTypeResolverTest {

    @Test
    public void resolve_Int() {
        Series<Object> s = Series.of(1, null, 2);
        ResolvedNumExp<?> resolved = NumberTypeResolver.resolve(s);

        assertSame(Integer.class, resolved.getType());
        assertSame(s, resolved.eval(s), "Series of a supported numeric type must not be converted");
    }

    @Test
    public void resolve_IntSeries() {
        Series<Integer> s = Series.ofInt(1, 2);
        ResolvedNumExp<?> resolved = NumberTypeResolver.resolve(s);

        assertSame(Integer.class, resolved.getType());
        assertSame(s, resolved.eval(s));
    }

    @Test
    public void resolve_DoubleSeries() {
        Series<Double> s = Series.ofDouble(1., 2.);
        ResolvedNumExp<?> resolved = NumberTypeResolver.resolve(s);

        assertSame(Double.class, resolved.getType());
        assertSame(s, resolved.eval(s));
    }

    @Test
    public void resolve_BigInteger() {
        Series<Object> s = Series.of(BigInteger.ONE, BigInteger.TEN);
        ResolvedNumExp<?> resolved = NumberTypeResolver.resolve(s);

        assertSame(BigInteger.class, resolved.getType());
        assertSame(s, resolved.eval(s));
    }

    @Test
    public void resolve_MixedNumbers() {
        assertThrows(IllegalArgumentException.class, () -> NumberTypeResolver.resolve(Series.of(1, 2L)));
        assertThrows(IllegalArgumentException.class, () -> NumberTypeResolver.resolve(Series.of(1, new BigDecimal("2.5"))));
    }

    @Test
    public void resolve_Strings() {
        Series<Object> s = Series.of("1.5", null, "2");
        ResolvedNumExp<?> resolved = NumberTypeResolver.resolve(s);

        assertSame(BigDecimal.class, resolved.getType());
        new SeriesAsserts(resolved.eval(s)).expectData(new BigDecimal("1.5"), null, new BigDecimal("2"));
    }

    @Test
    public void resolve_AllNulls() {
        Series<Object> s = Series.of(null, null);
        ResolvedNumExp<?> resolved = NumberTypeResolver.resolve(s);

        assertSame(BigDecimal.class, resolved.getType());
        new SeriesAsserts(resolved.eval(s)).expectData(null, null);
    }

    @Test
    public void resolve_Short() {
        Series<Object> s = Series.of((short) 1, (short) 2);
        ResolvedNumExp<?> resolved = NumberTypeResolver.resolve(s);

        assertSame(BigDecimal.class, resolved.getType());
        new SeriesAsserts(resolved.eval(s)).expectData(BigDecimal.valueOf(1), BigDecimal.valueOf(2));
    }

    @Test
    public void resolve_Scalar() {
        assertSame(Long.class, NumberTypeResolver.resolve((Object) 5L).getType());
        assertSame(BigDecimal.class, NumberTypeResolver.resolve((Object) null).getType());
        assertEquals(new BigDecimal("2.5"), NumberTypeResolver.resolve((Object) "2.5").reduce(Series.of()));
    }

    @Test
    public void resolve_Scalar_ParseableViaToString() {
        Object value = new Object() {
            @Override
            public String toString() {
                return "12.5";
            }
        };

        assertEquals(new BigDecimal("12.5"), NumberTypeResolver.resolve(value).reduce(Series.of()));
    }

    @Test
    public void resolve_ParseableViaToString() {
        Object value = new Object() {
            @Override
            public String toString() {
                return "7.25";
            }
        };

        Series<Object> s = Series.of(value, value);
        ResolvedNumExp<?> resolved = NumberTypeResolver.resolve(s);
        assertSame(BigDecimal.class, resolved.getType());
        new SeriesAsserts(resolved.eval(s)).expectData(new BigDecimal("7.25"), new BigDecimal("7.25"));
    }

    @Test
    public void resolve_UnparseableViaToString() {
        Object value = new Object() {
            @Override
            public String toString() {
                return "abc";
            }
        };

        // conversion of the series is lazy, so the error occurs when the data is accessed
        assertThrows(NumberFormatException.class, () -> NumberTypeResolver.resolve(Series.of(value, value)).eval(Series.of()).materialize());
    }
}
