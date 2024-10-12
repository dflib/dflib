package org.dflib.builder;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.Series;

import java.util.Objects;

public class DataFrameByColumnBuilder extends BaseDataFrameBuilder {

    public DataFrameByColumnBuilder(Index columnsIndex) {
        super(columnsIndex);
    }

    public DataFrame of(Series<?>... columns) {
        Objects.requireNonNull(columns);
        return new ColumnDataFrame(null, columnsIndex, columns);
    }

    public DataFrame ofIterable(Iterable<Series<?>> columns) {
        return new ColumnDataFrame(null, columnsIndex, toCollection(columns).toArray(new Series[0]));
    }
}
