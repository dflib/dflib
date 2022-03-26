package com.nhl.dflib.exp;

import com.nhl.dflib.Series;

/**
 * @since 0.11
 */
public class ConstExp<T> extends ScalarExp<T> {
    private volatile Series cached;

    public ConstExp(T value, Class<T> type) {
        super(value, type);
    }

    @Override
    protected Series doEval(int height, Object value) {
        if (cached == null) {
            cached = Series.singleValue(getType(), value, height);
        }
        return cached;
    }


}
