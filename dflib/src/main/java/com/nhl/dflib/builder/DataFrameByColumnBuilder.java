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
        return new ColumnDataFrame(columnsIndex, columnsArray(columns));
    }

    private Series<?>[] columnsArray(Iterable<Series<?>> columns) {
        Objects.requireNonNull(columns);

        if (columns instanceof Collection) {
            return ((Collection<Series<?>>) columns).toArray(new Series[0]);
        }

        List<Series<?>> list = new ArrayList<>();
        for (Series<?> s : columns) {
            list.add(s);
        }

        return list.toArray(new Series[0]);
    }
}
