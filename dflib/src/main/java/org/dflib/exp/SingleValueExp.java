package org.dflib.exp;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.series.SingleValueSeries;

/**
 * @since 2.0.0
 */
public class SingleValueExp<T> extends Exp0<T> {

    private final T val;

    public SingleValueExp(String opName, T val) {
        super(opName, val != null ? val.getClass() : Object.class);
        this.val = val;
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return new SingleValueSeries<>(val, df.height());
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return new SingleValueSeries<>(val, s.size());
    }

    @Override
    public T reduce(DataFrame df) {
        return val;
    }

    @Override
    public T reduce(Series<?> s) {
        return val;
    }
}
