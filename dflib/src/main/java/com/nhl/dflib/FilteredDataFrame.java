package com.nhl.dflib;

import com.nhl.dflib.filter.DataRowPredicate;
import com.nhl.dflib.print.InlinePrinter;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class FilteredDataFrame implements DataFrame {

    private DataFrame source;
    private DataRowPredicate rowFilter;

    public FilteredDataFrame(DataFrame source, DataRowPredicate rowFilter) {
        this.source = source;
        this.rowFilter = rowFilter;
    }

    @Override
    public Index getColumns() {
        return source.getColumns();
    }

    @Override
    public Iterator<Object[]> iterator() {
        return new Iterator<Object[]>() {

            private final Index columns = source.getColumns();
            private final Iterator<Object[]> delegateIt = source.iterator();

            private Object[] lastResolved;

            {
                rewind();
            }

            private void rewind() {
                lastResolved = null;
                while (delegateIt.hasNext()) {
                    Object[] next = delegateIt.next();
                    if (rowFilter.test(columns, next)) {
                        lastResolved = next;
                        break;
                    }
                }
            }

            @Override
            public boolean hasNext() {
                return lastResolved != null;
            }

            @Override
            public Object[] next() {

                if (lastResolved == null) {
                    throw new NoSuchElementException("No next element");
                }

                Object[] next = lastResolved;
                rewind();
                return next;
            }
        };
    }

    @Override
    public String toString() {
        return InlinePrinter.getInstance().print(new StringBuilder("FilteredDataFrame ["), this).append("]").toString();
    }
}
