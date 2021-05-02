package com.nhl.dflib.seriesexp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;

import java.util.Objects;

/**
 * An expression that evaluates to a column from a DataFrame identified either by name or position.
 *
 * @since 0.11
 */
public class ColumnExp<V> implements SeriesExp<V> {

    private final int position;
    private final String name;
    private final Class<V> type;

    public ColumnExp(String name, Class<V> type) {
        this.name = Objects.requireNonNull(name);
        this.position = -1;
        this.type = type;
    }

    public ColumnExp(int position, Class<V> type) {
        this.name = String.valueOf(position);
        this.position = position;
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
        return position >= 0 ? df.getColumn(position) : df.getColumn(name);
    }
}
