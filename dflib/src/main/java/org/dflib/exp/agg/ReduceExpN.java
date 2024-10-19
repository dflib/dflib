package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.exp.ExpN;

import java.util.function.Function;

/**
 * @since 2.0.0
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

        int w = args.length;
        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = args[i].eval(dff);
        }

        return op.apply(columns);
    }

    @Override
    public T reduce(Series<?> s) {

        Series<?> sf = filter != null ? s.select(filter) : s;
        int w = args.length;
        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = args[i].eval(sf);
        }

        return op.apply(columns);
    }
}
