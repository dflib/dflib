package com.nhl.dflib.row;

import com.nhl.dflib.Index;

import java.util.Collection;
import java.util.Iterator;

public class IterableRowDataFrame extends BaseRowDataFrame {

    private Iterable<Object[]> source;

    public IterableRowDataFrame(Index columns, Iterable<Object[]> source) {
        super(columns);
        this.source = source;
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

        return super.height();
    }
}
