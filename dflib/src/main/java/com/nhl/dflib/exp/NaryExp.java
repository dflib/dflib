package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;

import java.util.function.Function;

/**
 * @since 0.11
 */
public class NaryExp<V> implements Exp<V> {

    private final String name;
    private final Class<V> type;
    protected final Exp<V>[] parts;
    private final Function<Series<V>[], Series<V>> op;

    public NaryExp(String name, Class<V> type, Exp<V>[] parts, Function<Series<V>[], Series<V>> op) {

        if (parts.length == 0) {
            throw new IllegalArgumentException("Empty sub-expressions arrays");
        }

        this.name = name;
        this.type = type;
        this.parts = parts;
        this.op = op;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<V> getType() {
        return type;
    }

    @Override
    public Series<V> eval(DataFrame df) {
        int len = parts.length;
        Series<V>[] values = new Series[len];

        for (int i = 0; i < len; i++) {
            values[i] = parts[i].eval(df);
        }

        return op.apply(values);
    }
}
