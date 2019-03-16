package com.nhl.dflib.row;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.print.InlinePrinter;
import com.nhl.dflib.row.RowIterator;
import com.nhl.dflib.row.RowProxy;

import java.util.Iterator;
import java.util.List;

public class RowDataFrame implements DataFrame {

    private Index columns;
    private List<Object[]> rows;

    public RowDataFrame(Index columns, List<Object[]> rows) {
        this.columns = columns;
        this.rows = rows;
    }

    @Override
    public DataFrame materialize() {
        return this;
    }

    @Override
    public Index getColumns() {
        return columns;
    }

    @Override
    public int height() {
        return rows.size();
    }

    @Override
    public Iterator<RowProxy> iterator() {
        return RowIterator.overArrays(columns, rows);
    }

    @Override
    public String toString() {
        return InlinePrinter.getInstance().print(new StringBuilder("MaterializedDataFrame ["), this).append("]").toString();
    }
}
