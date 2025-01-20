package org.dflib.exp;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;

import java.util.Objects;

/**
 * A unary expression with a scalar argument.
 *
 * @since 2.0.0
 */
public class ScalarExp<T> implements Exp<T> {

    private final Class<T> type;
    protected final T value;

    static String scalarToQL(Object val) {
        // TODO: val is a bare object and may produce an ugly toString(). Need to be smarter about it

        boolean quotes = val != null && !(val instanceof Number);
        String unquoted = String.valueOf(val);
        return quotes ? "'" + unquoted + "'" : unquoted;
    }

    public ScalarExp(T value, Class<T> type) {
        this.value = value;
        this.type = Objects.requireNonNull(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScalarExp)) {
            return false;
        }
        ScalarExp<?> scalarExp = (ScalarExp<?>) o;
        return Objects.equals(type, scalarExp.type)
                && Objects.equals(value, scalarExp.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public String toQL() {
        return scalarToQL(value);
    }

    @Override
    public String toQL(DataFrame df) {
        return toQL();
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return Series.ofVal(value, df.height());
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return Series.ofVal(value, s.size());
    }

    @Override
    public T reduce(DataFrame df) {
        return value;
    }

    @Override
    public T reduce(Series<?> s) {
        return value;
    }
}
