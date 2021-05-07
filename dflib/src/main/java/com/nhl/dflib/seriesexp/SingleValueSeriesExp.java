package com.nhl.dflib.seriesexp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesExp;
import com.nhl.dflib.series.SingleValueSeries;

import java.util.Objects;

/**
 * @since 0.11
 */
public class SingleValueSeriesExp<T> implements SeriesExp<T> {

    private final Class<T> type;
    private final T value;

    public SingleValueSeriesExp(T value, Class<T> type) {
        this.value = value;
        this.type = Objects.requireNonNull(type);
    }

    @Override
    public String getName(DataFrame df) {
        return "$val";
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public Series<T> eval(DataFrame df) {
        return new SingleValueSeries<>(value, df.height());
    }
}
