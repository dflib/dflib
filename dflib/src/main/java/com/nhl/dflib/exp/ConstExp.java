package com.nhl.dflib.exp;

import com.nhl.dflib.Series;
import com.nhl.dflib.series.SingleValueSeries;

/**
 * @since 0.11
 */
public class ConstExp<T> extends ScalarExp<T> {

    public ConstExp(T value, Class<T> type) {
        super(value, type);
    }

    @Override
    protected Series<T> doEval(int height, T value) {
        // TODO: cache the result in the exp?

        // TODO: explore possible performance improvement by not converting scalars to Series at all, and providing a
        //   separate evaluation path instead.

        return new SingleValueSeries<>(value, height);
    }
}
