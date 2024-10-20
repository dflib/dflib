package org.dflib.exp.agg;

import org.dflib.Condition;
import org.dflib.DataFrame;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.Exp0;
import org.dflib.series.IntSingleValueSeries;


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
        int c = filter != null ? filter.eval(df).countTrue() : df.height();
        return new IntSingleValueSeries(c, 1);
    }

    @Override
    public Series<Integer> eval(Series<?> s) {
        int c = filter != null ? filter.eval(s).countTrue() : s.size();
        return new IntSingleValueSeries(c, 1);
    }
}
