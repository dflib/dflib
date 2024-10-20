package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.ExpN;

import java.util.function.Function;

/**
 * @since 1.1.0
 */
public class ReduceExpN<T> extends ExpN<T> {

    private final Function<Series[], T> op;
    private final Condition filter;

    public ReduceExpN(String opName, Class<T> type, Exp<?>[] args, Function<Series[], T> op, Condition filter) {
        super(opName, type, args);
        this.op = op;
        this.filter = filter;
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return super.eval(filter != null ? df.rows(filter).select() : df);
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return super.eval(filter != null ? s.select(filter) : s);
    }

    @Override
    protected Series<T> doEval(int height, Series<?>[] args) {
        T val = op.apply(args);
        return Series.ofVal(val, 1);
    }
}
