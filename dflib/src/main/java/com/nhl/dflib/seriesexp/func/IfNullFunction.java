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
    public String getName(DataFrame df) {
        return "ifnull(" + exp.getName(df) + "," + ifNullExp.getName(df) + ")";
    }

    @Override
    public Class<T> getType() {
        return exp.getType();
    }

    @Override
    public Series<T> eval(DataFrame df) {
        Series<T> s = exp.eval(df);
        IntSeries nulls = s.index(v -> v == null);

        int nullsLen = nulls.size();
        if (nullsLen == 0) {
            return s;
        }

        Series<T> nullReplacements = ifNullExp.eval(df.selectRows(nulls));

        // TODO: "s" is not a primitive Series by definition, but replacing nulls may produce a primitive-compatible
        //  Series. See if we can exploit this fact for performance optimization

        Object[] vals = new Object[s.size()];
        s.copyTo(vals, 0, 0, vals.length);

        for (int i = 0; i < nullsLen; i++) {
            vals[nulls.getInt(i)] = nullReplacements.get(i);
        }

        return (Series<T>) Series.forData(vals);
    }
}
