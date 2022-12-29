package com.nhl.dflib.builder;

import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;

import java.util.Objects;

/**
 * @since 0.16
 */
public class DataFrameByColumnBuilder extends BaseDataFrameBuilder {

    public DataFrameByColumnBuilder(Index columnsIndex) {
        super(columnsIndex);
    }

    public DataFrame of(Series<?>... columns) {
        Objects.requireNonNull(columns);
        return new ColumnDataFrame(columnsIndex, columns);
    }

    public DataFrame ofIterable(Iterable<Series<?>> columns) {
        return new ColumnDataFrame(columnsIndex, toCollection(columns).toArray(new Series[0]));
    }
}
