package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.Exp0;


public class CountExp extends Exp0<Integer> implements NumExp<Integer> {

    private static final CountExp instance = new CountExp(null);

    public static CountExp getInstance() {
        return instance;
    }

    private final Condition filter;

    public CountExp(Condition filter) {
        super("count", Integer.class);
        this.filter = filter;
    }

    @Override
    public Series<Integer> eval(DataFrame df) {
        return Series.ofVal(reduce(df), df.height());
    }

    @Override
    public Series<Integer> eval(Series<?> s) {
        return Series.ofVal(reduce(s), s.size());
    }

    @Override
    public Integer reduce(DataFrame df) {
        return filter != null ? filter.eval(df).countTrue() : df.height();
    }

    @Override
    public Integer reduce(Series<?> s) {
        return filter != null ? filter.eval(s).countTrue() : s.size();
    }
}
