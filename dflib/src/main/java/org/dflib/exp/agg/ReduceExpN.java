package org.dflib.exp.agg;

import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.ExpN;

import java.util.function.Function;

/**
 * @since 1.1.0
 */
public class ReduceExpN<T> extends ExpN<T> {

    private final Function<Series[], T> op;

    public ReduceExpN(String opName, Class<T> type, Exp<?>[] args, Function<Series[], T> op) {
        super(opName, type, args);
        this.op = op;
    }

    @Override
    protected Series<T> doEval(int height, Series<?>[] args) {
        T val = op.apply(args);
        return Series.ofVal(val, 1);
    }
}
