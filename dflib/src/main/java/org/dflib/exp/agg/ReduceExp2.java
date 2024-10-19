package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.Exp2;

import java.util.function.BiFunction;

/**
 * @since 2.0.0
 */
public class ReduceExp2<L, R, T> extends Exp2<L, R, T> {

    private final BiFunction<Series<L>, Series<R>, T> op;
    private final Condition filter;

    public ReduceExp2(String opName, Class<T> type, Exp<L> left, Exp<R> right, BiFunction<Series<L>, Series<R>, T> op, Condition filter) {
        super(opName, type, left, right);
        this.op = op;
        this.filter = filter;
    }

    @Override
    public String toQL() {
        // aggregators are usually functions, not operators. So redefine super...
        return opName + "(" + left.toQL() + "," + right.toQL() + ")";
    }

    @Override
    public String toQL(DataFrame df) {
        // aggregators are usually functions, not operators. So redefine super...
        return opName + "(" + left.toQL(df) + "," + right.toQL(df) + ")";
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return Series.ofVal(reduce(s), s.size());
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return Series.ofVal(reduce(df), df.height());
    }

    @Override
    public T reduce(DataFrame df) {
        DataFrame dff = filter != null ? df.rows(filter).select() : df;
        return op.apply(left.eval(dff), right.eval(dff));
    }

    @Override
    public T reduce(Series<?> s) {
        Series<?> sf = filter != null ? s.select(filter) : s;
        return op.apply(left.eval(sf), right.eval(sf));
    }
}
