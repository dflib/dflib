package com.nhl.dflib.row;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;

import java.util.Iterator;
import java.util.List;

public class RowDataFrame extends BaseRowDataFrame {

    private List<Object[]> rows;

    public RowDataFrame(Index columns, List<Object[]> rows) {
        super(columns);
        this.rows = rows;
    }

    @Override
    public DataFrame materialize() {
        return this;
    }

    @Override
    public int height() {
        return rows.size();
    }

    @Override
    public Iterator<RowProxy> iterator() {
        return RowIterator.overArrays(columns, rows);
    }
}
