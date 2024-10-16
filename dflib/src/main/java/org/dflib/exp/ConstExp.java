package org.dflib.exp;

import org.dflib.Series;


public class ConstExp<T> extends ExpScalar1<T> {

    public ConstExp(T value, Class<T> type) {
        super(value, type);
    }

    @Override
    protected Series<T> doEval(int height) {
        return Series.ofVal(value, height);
    }
}
