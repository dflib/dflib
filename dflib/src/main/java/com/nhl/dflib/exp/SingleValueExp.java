package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.SingleValueSeries;

import java.util.Objects;

/**
 * @since 0.11
 */
public class SingleValueExp<V> implements Exp<V> {

    private final Class<V> type;
    private final V value;

    public SingleValueExp(V value, Class<V> type) {
        this.value = value;
        this.type = Objects.requireNonNull(type);
    }

    @Override
    public String getName() {
        return "$val";
    }

    @Override
    public Class<V> getType() {
        return type;
    }

    @Override
    public Series<V> eval(DataFrame df) {
        return new SingleValueSeries<>(value, df.height());
    }
}
