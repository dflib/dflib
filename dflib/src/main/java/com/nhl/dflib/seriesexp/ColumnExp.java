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
public class ColumnExp<T> implements SeriesExp<T> {

    private final int position;
    private final String name;
    private final Class<T> type;

    public ColumnExp(String name, Class<T> type) {
        this.name = Objects.requireNonNull(name);
        this.position = -1;
        this.type = type;
    }

    public ColumnExp(int position, Class<T> type) {
        this.name = String.valueOf(position);
        this.position = position;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return position >= 0 ? df.getColumn(position) : df.getColumn(name);
    }
}
