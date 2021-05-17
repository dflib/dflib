package com.nhl.dflib.exp.agg;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.NumExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.exp.Exp0;
import com.nhl.dflib.series.SingleValueSeries;

/**
 * @since 0.11
 */
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

        // TODO: IntSingleValueSeries
        return new SingleValueSeries<>(c, 1);
    }

    @Override
    public Series<Integer> eval(Series<?> s) {
        int c = s.size();

        // TODO: IntSingleValueSeries
        return new SingleValueSeries<>(c, 1);
    }
}
