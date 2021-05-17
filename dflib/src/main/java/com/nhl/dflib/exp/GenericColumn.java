package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.Exp;

import java.util.Objects;

/**
 * An expression that evaluates to a column from a DataFrame identified either by name or position.
 *
 * @since 0.11
 */
public class GenericColumn<T> implements Exp<T> {

    protected final int position;
    protected final String name;
    private final Class<T> type;

    public GenericColumn(String name, Class<T> type) {
        this.type = Objects.requireNonNull(type);
        this.name = Objects.requireNonNull(name);
        this.position = -1;
    }

    public GenericColumn(int position, Class<T> type) {
        if (position < 0) {
            throw new IllegalArgumentException("Position must nit be negative: " + position);
        }

        this.type = Objects.requireNonNull(type);
        this.name = null;
        this.position = position;
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public String toQL() {
        return position >= 0 ? "$col(" + position + ")" : name;
    }

    @Override
    public String toQL(DataFrame df) {
        return position >= 0 ? df.getColumnsIndex().getLabel(position) : name;
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return position >= 0 ? df.getColumn(position) : df.getColumn(name);
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return (Series<T>) s;
    }
}
