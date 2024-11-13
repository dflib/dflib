package org.dflib.exp;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;

import java.util.Objects;

/**
 * A unary expression with a scalar argument.
 */
public abstract class ExpScalar1<T> implements Exp<T> {

    private final Class<T> type;
    protected final T value;

    static String scalarToQL(Object val) {
        // TODO: val is a bare object and may produce an ugly toString(). Need to be smarter about it

        boolean quotes = val != null && !(val instanceof Number);
        String unquoted = String.valueOf(val);
        return quotes ? "'" + unquoted + "'" : unquoted;
    }

    public ExpScalar1(T value, Class<T> type) {
        this.value = value;
        this.type = Objects.requireNonNull(type);
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
        return doEval(df.height());
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return doEval(s.size());
    }

    protected abstract Series<T> doEval(int height);
}
