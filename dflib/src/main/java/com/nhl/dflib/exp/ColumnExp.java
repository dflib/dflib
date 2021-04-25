package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;

/**
 * An expression that returns an unchanged named column from a DataFrame.
 *
 * @since 0.11
 */
public class ColumnExp<V> implements ValueExp<V> {

    private final String name;
    private final Class<V> type;

    public ColumnExp(String name, Class<V> type) {
        this.name = name;
        this.type = type;
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
        return df.getColumn(name);
    }
}
