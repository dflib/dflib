package org.dflib.exp.agg;

import org.dflib.DataFrame;
import org.dflib.NumExp;
import org.dflib.Series;
import org.dflib.exp.Exp0;
import org.dflib.series.IntSingleValueSeries;


public class CountExp extends Exp0<Integer> implements NumExp<Integer> {

    private static final CountExp instance = new CountExp();

    public static CountExp getInstance() {
        return instance;
    }

    public CountExp() {
        super("count", Integer.class);
    }

    @Override
    public Series<Integer> eval(DataFrame df) {
        int c = df.height();

        return new IntSingleValueSeries(c, 1);
    }

    @Override
    public Series<Integer> eval(Series<?> s) {
        int c = s.size();

        return new IntSingleValueSeries(c, 1);
    }
}
