package org.dflib.exp.num;

import org.dflib.Series;
import org.dflib.series.ObjectSeries;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class NumberTypeResolverTest {

    @Test
    public void trustedSeries_skipsScan() {
        CountingSeries delegate = new CountingSeries(Series.of(new BigDecimal("1.5"), new BigDecimal("2.5")));
        Series<? extends Number> trusted = new ResolvedNominalSeries<>(BigDecimal.class, delegate, false);

        Series<?> resolved = NumberTypeResolver.resolve(trusted, (f, e) -> e)
                .eval(Series.ofVal(null, 2));

        assertEquals(2, resolved.size());
        assertEquals(0, delegate.getCount);
    }

    @Test
    public void unknownSeries_scans() {
        CountingSeries delegate = new CountingSeries(Series.of(new BigDecimal("1.5"), new BigDecimal("2.5")));

        Series<?> resolved = NumberTypeResolver.resolve(delegate, (f, e) -> e)
                .eval(Series.ofVal(null, 2));

        assertEquals(2, resolved.size());
        assertEquals(3, delegate.getCount);
    }

    @Test
    public void convertedSeries_returnsTrusted() {
        Series<?> resolved = NumberTypeResolver.resolve(
                Series.of(1, new BigDecimal("2.5")),
                (f, e) -> e).eval(Series.ofVal(null, 2));

        assertSame(BigDecimal.class, resolved.getNominalType());
    }

    @Test
    public void scalar_parseableViaToString() {
        Object value = new Object() {
            @Override
            public String toString() {
                return "12.5";
            }
        };

        Number resolved = NumberTypeResolver.resolve(value, (f, e) -> e)
                .reduce(Series.ofVal(null, 1));

        assertEquals(new BigDecimal("12.5"), resolved);
    }

    @Test
    public void series_parseableViaToString() {
        Object value = new Object() {
            @Override
            public String toString() {
                return "7.25";
            }
        };

        Series<?> resolved = NumberTypeResolver.resolve(
                Series.of(value, 1),
                (f, e) -> e).eval(Series.ofVal(null, 2));

        assertSame(BigDecimal.class, resolved.getNominalType());
        assertEquals(new BigDecimal("7.25"), resolved.get(0));
        assertEquals(new BigDecimal("1"), resolved.get(1));
    }

    @Test
    public void series_unparseableViaToString() {
        Object value = new Object() {
            @Override
            public String toString() {
                return "abc";
            }
        };

        assertThrows(NumberFormatException.class, () -> NumberTypeResolver.resolve(
                Series.of(value, 1),
                (f, e) -> e));
    }

    static class CountingSeries extends ObjectSeries<Number> {

        private final Series<? extends Number> delegate;
        int getCount;

        CountingSeries(Series<? extends Number> delegate) {
            super(Object.class);
            this.delegate = delegate;
        }

        @Override
        public int size() {
            return delegate.size();
        }

        @Override
        public Number get(int index) {
            getCount++;
            return delegate.get(index);
        }

        @Override
        public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
            for (int i = 0; i < len; i++) {
                to[toOffset + i] = get(fromOffset + i);
            }
        }

        @Override
        public Series<Number> materialize() {
            return this;
        }

        @Override
        public Series<Number> fillNulls(Number value) {
            return delegate.unsafeCastAs(Number.class).fillNulls(value);
        }

        @Override
        public Series<Number> fillNullsFromSeries(Series<? extends Number> values) {
            return delegate.unsafeCastAs(Number.class).fillNullsFromSeries(values);
        }

        @Override
        public Series<Number> fillNullsBackwards() {
            return delegate.unsafeCastAs(Number.class).fillNullsBackwards();
        }

        @Override
        public Series<Number> fillNullsForward() {
            return delegate.unsafeCastAs(Number.class).fillNullsForward();
        }
    }
}
