package com.nhl.dflib.row;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.print.InlinePrinter;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class HeadRowDataFrame implements DataFrame {

    private DataFrame source;
    private int len;

    public HeadRowDataFrame(DataFrame source, int len) {
        this.source = source;
        this.len = len;
    }

    @Override
    public DataFrame head(int len) {
        return len >= this.len ? this : source.head(len);
    }

    @Override
    public Index getColumns() {
        return source.getColumns();
    }

    @Override
    public Iterator<RowProxy> iterator() {

        return new Iterator<RowProxy>() {

            private int counter = 0;
            private final Iterator<RowProxy> it = source.iterator();

            @Override
            public boolean hasNext() {
                return counter < len && it.hasNext();
            }

            @Override
            public RowProxy next() {
                if (counter >= len) {
                    throw new NoSuchElementException("Past the end of the iterator");
                }

                counter++;
                return it.next();
            }
        };
    }

    @Override
    public String toString() {
        return InlinePrinter.getInstance().print(new StringBuilder("HeadDataFrame ["), this).append("]").toString();
    }
}
