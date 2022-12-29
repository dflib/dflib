package com.nhl.dflib.builder;

import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @since 0.16
 */
public class DataFrameByColumnBuilder {

    private final Index columnsIndex;

    public DataFrameByColumnBuilder(Index columnsIndex) {
        this.columnsIndex = columnsIndex;
    }

    public DataFrame array(Series<?>... columns) {
        Objects.requireNonNull(columns);
        return new ColumnDataFrame(columnsIndex, columns);
    }

    public DataFrame iterable(Iterable<Series<?>> columns) {
        return new ColumnDataFrame(columnsIndex, toCollection(columns).toArray(new Series[0]));
    }

    private <T> Collection<T> toCollection(Iterable<T> columns) {
        Objects.requireNonNull(columns);

        if (columns instanceof Collection) {
            return ((Collection<T>) columns);
        }

        List<T> list = new ArrayList<>();
        columns.forEach(list::add);
        return list;
    }
}
