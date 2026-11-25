package org.dflib.exp.num;

import org.dflib.DataFrame;
import org.dflib.NumExp;
import org.dflib.Series;

/**
 * For internal use only. Wraps an already resolved Series and ignores the eval/reduce argument.
 */
class ResolvedNumExp<N extends Number> implements NumExp<N> {

    private final Class<N> type;
    private final Series<N> series;

    ResolvedNumExp(Class<N> type, Series<N> series) {
        this.type = type;
        this.series = series;
    }

    @Override
    public Class<N> getType() {
        return type;
    }

    @Override
    public Series<N> eval(Series<?> s) {
        return series;
    }

    @Override
    public N reduce(Series<?> s) {
        return series.first();
    }

    @Override
    public String toQL() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toQL(DataFrame df) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Series<N> eval(DataFrame df) {
        throw new UnsupportedOperationException();
    }

    @Override
    public N reduce(DataFrame df) {
        throw new UnsupportedOperationException();
    }
}
