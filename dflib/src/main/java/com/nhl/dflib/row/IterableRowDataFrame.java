package com.nhl.dflib.row;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.print.InlinePrinter;

import java.util.Collection;
import java.util.Iterator;

public class IterableRowDataFrame implements DataFrame {

    private Iterable<Object[]> source;
    private Index columns;

    public IterableRowDataFrame(Index columns, Iterable<Object[]> source) {
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
