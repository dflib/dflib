package com.nhl.dflib;

import com.nhl.dflib.print.InlinePrinter;
import com.nhl.dflib.row.RowIterator;
import com.nhl.dflib.row.RowProxy;

import java.util.Iterator;
import java.util.List;

public class MaterializedDataFrame implements DataFrame {

    private Index columns;
    private List<Object[]> materialized;

    public MaterializedDataFrame(Index columns, List<Object[]> materialized) {
        this.columns = columns;
        this.materialized = materialized;
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
        return materialized.size();
    }

    @Override
    public Iterator<RowProxy> iterator() {
        return RowIterator.overArrays(columns, materialized);
    }

    @Override
    public String toString() {
        return InlinePrinter.getInstance().print(new StringBuilder("MaterializedDataFrame ["), this).append("]").toString();
    }
}
