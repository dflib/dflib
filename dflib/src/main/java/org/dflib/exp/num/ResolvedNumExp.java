package org.dflib.exp.num;

import org.dflib.DataFrame;
import org.dflib.NumExp;
import org.dflib.Series;

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
    public String toQL() {
        return "<resolved>";
    }

    @Override
    public String toQL(DataFrame df) {
        return toQL();
    }

    @Override
    public Series<N> eval(DataFrame df) {
        return series;
    }

    @Override
    public Series<N> eval(Series<?> s) {
        return series;
    }

    @Override
    public N reduce(DataFrame df) {
        return series.first();
    }

    @Override
    public N reduce(Series<?> s) {
        return series.first();
    }
}
