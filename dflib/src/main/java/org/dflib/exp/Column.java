package org.dflib.exp;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.Exp;

import java.util.Objects;

/**
 * An expression that evaluates to a named or a positional column from a DataFrame. In case of Series, it evaluates to
 * the Series itself.
 */
public class Column<T> implements Exp<T> {

    protected final int position;
    protected final String name;
    private final Class<T> type;

    public Column(String name, Class<T> type) {
        this.type = Objects.requireNonNull(type);
        this.name = Objects.requireNonNull(name);
        this.position = -1;
    }

    public Column(int position, Class<T> type) {
        if (position < 0) {
            throw new IllegalArgumentException("Position must not be negative: " + position);
        }

        this.type = Objects.requireNonNull(type);
        this.name = null;
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Column<?> column = (Column<?>) o;
        return position == column.position
                && Objects.equals(name, column.name)
                && Objects.equals(type, column.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, name, type);
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
        return position >= 0 ? df.getColumnsIndex().get(position) : name;
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return position >= 0 ? df.getColumn(position) : df.getColumn(name);
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return (Series<T>) s;
    }

    @Override
    public T reduce(DataFrame df) {
        return eval(df).first();
    }

    @Override
    public T reduce(Series<?> s) {
        return eval(s).first();
    }
}
