package com.nhl.dflib.exp.func;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;

/**
 * @since 0.11
 */
public class IfNullFunction<V> implements Exp<V> {

    private final Exp<V> exp;
    private final Exp<V> ifNullExp;

    public IfNullFunction(Exp<V> exp, Exp<V> ifNullExp) {
        this.exp = exp;
        this.ifNullExp = ifNullExp;
    }

    @Override
    public String getName() {
        return "ifnull(" + exp.getName() + "," + ifNullExp.getName() + ")";
    }

    @Override
    public Class<V> getType() {
        return exp.getType();
    }

    @Override
    public Series<V> eval(DataFrame df) {
        Series<V> s = exp.eval(df);
        IntSeries nulls = s.index(v -> v == null);

        int nullsLen = nulls.size();
        if (nullsLen == 0) {
            return s;
        }

        Series<V> nullReplacements = ifNullExp.eval(df.selectRows(nulls));

        // TODO: "s" is not a primitive Series by definition, but replacing nulls may produce a primitive-compatible
        //  Series. See if we can exploit this fact for performance optimization

        Object[] vals = new Object[s.size()];
        s.copyTo(vals, 0, 0, vals.length);

        for (int i = 0; i < nullsLen; i++) {
            vals[nulls.getInt(i)] = nullReplacements.get(i);
        }

        return (Series<V>) Series.forData(vals);
    }
}
