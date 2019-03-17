package com.nhl.dflib.row;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.map.RowMapper;

public abstract class BaseRowDataFrame implements DataFrame {

    protected Index columns;

    public BaseRowDataFrame(Index columns) {
        this.columns = columns;
    }

    @Override
    public Index getColumns() {
        return columns;
    }

    /**
     * Resolves this DataFrame to an implementation that evaluates internal mapping/concat/filter functions no more
     * than once, reusing the first evaluation result for subsequent iterations. Certain operations in DataFrame, such as
     * {@link #map(Index, RowMapper)}, etc. are materialized by default.
     *
     * @return a DataFrame optimized for multiple iterations, calls to {@link #height()}, etc.
     */
    @Override
    public DataFrame materialize() {
        return new MaterializableRowDataFrame(this);
    }
}
