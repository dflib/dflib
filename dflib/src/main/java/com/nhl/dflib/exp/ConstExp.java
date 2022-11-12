package com.nhl.dflib.exp;

import com.nhl.dflib.Series;
import com.nhl.dflib.series.SingleValueSeries;

/**
 * @since 0.11
 */
public class ConstExp<T> extends ExpScalar1<T> {

    public ConstExp(T value, Class<T> type) {
        super(value, type);
    }

    @Override
    protected Series<T> doEval(int height) {
        return new SingleValueSeries<>(this.value, height);
    }
}
