package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.Exp1;

/**
 * @since 2.0.0
 */
public class LastExp<T> extends Exp1<T, T> {

    private final Condition filter;

    public LastExp(Class<T> type, Exp<T> exp, Condition filter) {
        super("last", type, exp);
        this.filter = filter;
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return super.eval(prefilter(df));
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return super.eval(prefilter(s));
    }

    @Override
    protected Series<T> doEval(Series<T> s) {
        return Series.ofVal(s.last(), 1);
    }

    private DataFrame prefilter(DataFrame df) {
        if (filter == null) {
            return df;
        }
        int index = filter.firstMatch(df);
        return index < 0 ? DataFrame.empty(df.getColumnsIndex()) : df.rows(index).select();
    }

    private Series<?> prefilter(Series<?> s) {
        if (filter == null) {
            return s;
        }
        int index = filter.firstMatch(s);
        return index < 0 ? s.select() : s.select(index);
    }
}
