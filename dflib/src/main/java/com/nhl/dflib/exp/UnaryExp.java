package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.ValueMapper;

/**
 * @since 0.11
 */
public class UnaryExp<F, V> implements Exp<V> {

    private final ValueMapper<F, V> mapper;
    private final Exp<F> exp;
    private final Class<V> type;

    public UnaryExp(Exp<F> exp, Class<V> type, ValueMapper<F, V> mapper) {
        this.exp = exp;
        this.type = type;
        this.mapper = mapper;
    }

    @Override
    public String getName() {
        return exp.getName();
    }

    @Override
    public Class<V> getType() {
        return type;
    }

    @Override
    public Series<V> eval(DataFrame df) {
        return exp.eval(df).map(mapper);
    }
}
