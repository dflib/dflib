package com.nhl.dflib.exp.filter;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.Condition;
import com.nhl.dflib.Exp;
import com.nhl.dflib.series.SingleValueSeries;

/**
 * @since 0.11
 */
public class PreFilteredCountExp implements Exp<Integer> {

    private final Condition filter;

    public PreFilteredCountExp(Condition filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return getName();
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
    public Class<Integer> getType() {
        return Integer.class;
    }

    @Override
    public Series<Integer> eval(DataFrame df) {

        // optmization: not rebuilding a filtered DataFrame ... Just count filter index
        int c = filter.eval(df).countTrue();

        // TODO: IntSingleValueSeries
        return new SingleValueSeries<>(c, 1);
    }

    @Override
    public Series<Integer> eval(Series<?> s) {
        // optimization: not rebuilding a filtered DataFrame ... Just count filter index
        int c = filter.eval(s).countTrue();

        // TODO: IntSingleValueSeries
        return new SingleValueSeries<>(c, 1);
    }
}
