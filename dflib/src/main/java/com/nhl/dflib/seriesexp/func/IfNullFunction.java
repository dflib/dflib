package com.nhl.dflib.seriesexp.func;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;

/**
 * @since 0.11
 */
public class IfNullFunction<T> implements SeriesExp<T> {

    private final SeriesExp<T> exp;
    private final SeriesExp<T> ifNullExp;

    public IfNullFunction(SeriesExp<T> exp, SeriesExp<T> ifNullExp) {
        this.exp = exp;
        this.ifNullExp = ifNullExp;
    }

    @Override
    public Class<T> getType() {
        return exp.getType();
    }

    @Override
    public String getName() {
        return "ifnull(" + exp.getName() + "," + ifNullExp.getName() + ")";
    }

    @Override
    public String getName(DataFrame df) {
        return "ifnull(" + exp.getName(df) + "," + ifNullExp.getName(df) + ")";
    }

    @Override
    public Series<T> eval(DataFrame df) {
        Series<T> data = exp.eval(df);
        IntSeries nullsIndex = data.index(v -> v == null);

        int nullsLen = nullsIndex.size();
        if (nullsLen == 0) {
            return data;
        }

        return evalMergeReplacements(data, ifNullExp.eval(df.selectRows(nullsIndex)), nullsIndex);
    }

    @Override
    public Series<T> eval(Series<?> s) {
        Series<T> data = exp.eval(s);
        IntSeries nullsIndex = data.index(v -> v == null);

        if (nullsIndex.size() == 0) {
            return data;
        }

        return evalMergeReplacements(data, ifNullExp.eval(s.select(nullsIndex)), nullsIndex);
    }

    protected Series<T> evalMergeReplacements(Series<T> data, Series<T> nullReplacements, IntSeries nullsIndex) {
        // TODO: "data" is not a primitive Series by definition, but replacing nulls may produce a primitive-compatible
        //  Series. See if we can exploit this fact for performance optimization

        Object[] vals = new Object[data.size()];
        data.copyTo(vals, 0, 0, vals.length);

        int nullsLen = nullsIndex.size();

        for (int i = 0; i < nullsLen; i++) {
            vals[nullsIndex.getInt(i)] = nullReplacements.get(i);
        }

        return (Series<T>) Series.forData(vals);
    }


}
