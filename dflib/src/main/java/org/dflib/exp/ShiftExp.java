package org.dflib.exp;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;

import java.util.Objects;

/**
 * @since 1.1.0
 */
public class ShiftExp<T> implements Exp<T> {

    private final Exp<T> delegate;
    private final int offset;
    private final T filler;

    public ShiftExp(Exp<T> delegate, int offset, T filler) {
        this.delegate = Objects.requireNonNull(delegate);
        this.offset = offset;
        this.filler = filler;
    }

    @Override
    public Class<T> getType() {
        return delegate.getType();
    }

    @Override
    public String toQL() {
        return "shift(" + delegate.toQL() + "," + offset + "," + ExpScalar1.scalarToQL(filler) + ")";
    }

    @Override
    public String toQL(DataFrame df) {
        return "shift(" + delegate.toQL(df) + "," + offset + "," + ExpScalar1.scalarToQL(filler) + ")";
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return delegate.eval(df).shift(offset, filler);
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return delegate.eval(s).shift(offset, filler);
    }
}
