package com.nhl.dflib.filter;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.print.InlinePrinter;
import com.nhl.dflib.row.RowProxy;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class FilteredDataFrame implements DataFrame {

    private DataFrame source;
    private RowPredicate rowFilter;

    public FilteredDataFrame(DataFrame source, RowPredicate rowFilter) {
        this.source = source;
        this.rowFilter = rowFilter;
    }

    @Override
    public Index getColumns() {
        return source.getColumns();
    }

    @Override
    public Iterator<RowProxy> iterator() {
        return new Iterator<RowProxy>() {

            private final Iterator<RowProxy> it = source.iterator();

            // extra caution needs to be taken when caching RowProxy.. Specifically, we can't rewind the underlying
            // Iterator until the entry is consumed by this iterator's caller
            private RowProxy row;
            private boolean rowUnconsumed;

            private void rewindIfNeeded() {

                if (rowUnconsumed) {
                    return;
                }

                row = null;
                while (it.hasNext()) {
                    RowProxy next = it.next();
                    if (rowFilter.test(next)) {
                        row = next;
                        break;
                    }
                }

                rowUnconsumed = true;
            }

            @Override
            public boolean hasNext() {
                rewindIfNeeded();
                return row != null;
            }

            @Override
            public RowProxy next() {

                rewindIfNeeded();

                if (row == null) {
                    throw new NoSuchElementException("No next element");
                }

                rowUnconsumed = false;
                return row;
            }
        };
    }

    @Override
    public String toString() {
        return InlinePrinter.getInstance().print(new StringBuilder("FilteredDataFrame ["), this).append("]").toString();
    }
}
