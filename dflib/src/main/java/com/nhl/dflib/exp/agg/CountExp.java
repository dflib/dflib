package com.nhl.dflib.exp.agg;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.Exp;
import com.nhl.dflib.series.SingleValueSeries;

/**
 * @since 0.11
 */
public class CountExp implements Exp<Integer> {

    private static final CountExp instance = new CountExp();

    public static CountExp getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public String getName() {
        return "count";
    }

    @Override
    public String getName(DataFrame df) {
        return "count";
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
