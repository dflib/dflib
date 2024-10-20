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

    public ScalarExp(T value, Class<T> type) {
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
        boolean quotes = value != null && !(value instanceof Number);
        String unquoted = String.valueOf(value);
        return quotes ? "'" + unquoted + "'" : unquoted;
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

    protected Series<T> doEval(int height) {
        return Series.ofVal(value, height);
    }
}
