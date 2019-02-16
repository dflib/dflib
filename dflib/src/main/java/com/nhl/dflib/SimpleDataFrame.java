package com.nhl.dflib;

import com.nhl.dflib.print.InlinePrinter;
import com.nhl.dflib.row.RowIterator;
import com.nhl.dflib.row.RowProxy;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class SimpleDataFrame implements DataFrame {

    private Iterable<Object[]> source;
    private Index columns;

    protected SimpleDataFrame(Index columns) {
        this(columns, Collections.emptyList());
    }

    protected SimpleDataFrame(Index columns, Iterable<Object[]> source) {
        this.source = source;
        this.columns = columns;
    }

    @Override
    public Index getColumns() {
        return columns;
    }

    @Override
    public Iterator<RowProxy> iterator() {
        return RowIterator.overArrays(columns, source);
    }

    @Override
    public int height() {

        // avoid iteration if possible
        if (source instanceof Collection) {
            return ((Collection) source).size();
        }

        return DataFrame.super.height();
    }

    @Override
    public String toString() {
        return InlinePrinter.getInstance().print(new StringBuilder("SimpleDataFrame ["), this).append("]").toString();
    }
}
