package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.Exp;
import com.nhl.dflib.series.SingleValueSeries;

import java.util.Objects;

/**
 * @since 0.11
 */
public class SingleValueExp<T> implements Exp<T> {

    private final Class<T> type;
    private final T value;

    public SingleValueExp(T value, Class<T> type) {
        this.value = value;
        this.type = Objects.requireNonNull(type);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public String getName() {
        boolean quotes = value != null && !(value instanceof Number);
        String unquoted = String.valueOf(value);
        return quotes ? "'" + unquoted + "'" : unquoted;
    }

    @Override
    public String getName(DataFrame df) {
        return getName();
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return new SingleValueSeries<>(value, df.height());
    }

    @Override
    public Series<T> eval(Series<?> s) {
        return new SingleValueSeries<>(value, s.size());
    }
}
