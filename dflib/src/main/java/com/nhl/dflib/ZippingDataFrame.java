package com.nhl.dflib;

import com.nhl.dflib.map.CombineContext;
import com.nhl.dflib.map.DataRowCombiner;
import com.nhl.dflib.print.InlinePrinter;

import java.util.Iterator;

public class ZippingDataFrame implements DataFrame {

    private DataFrame leftSource;
    private DataFrame rightSource;
    private Index columns;
    private DataRowCombiner rowCombiner;

    public ZippingDataFrame(
            Index columns,
            DataFrame leftSource,
            DataFrame rightSource,
            DataRowCombiner rowCombiner) {

        this.leftSource = leftSource;
        this.rightSource = rightSource;
        this.columns = columns;
        this.rowCombiner = rowCombiner;
    }

    @Override
    public Index getColumns() {
        return columns;
    }

    @Override
    public long height() {
        return leftSource.height();
    }

    @Override
    public Iterator<Object[]> iterator() {
        return new Iterator<Object[]>() {

            private final CombineContext context = new CombineContext(leftSource.getColumns(), rightSource.getColumns(), columns);
            private final Iterator<Object[]> leftIt = ZippingDataFrame.this.leftSource.iterator();
            private final Iterator<Object[]> rightIt = ZippingDataFrame.this.rightSource.iterator();

            @Override
            public boolean hasNext() {
                return leftIt.hasNext() && rightIt.hasNext();
            }

            @Override
            public Object[] next() {
                return rowCombiner.combine(context, leftIt.next(), rightIt.next());
            }
        };
    }

    @Override
    public String toString() {
        return InlinePrinter.getInstance().print(new StringBuilder("ZipDataFrame ["), this).append("]").toString();
    }
}
