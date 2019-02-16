package com.nhl.dflib.concat;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.print.InlinePrinter;
import com.nhl.dflib.row.ArrayRowBuilder;
import com.nhl.dflib.row.ArrayRowProxy;
import com.nhl.dflib.row.RowProxy;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class VConcatDataFrame implements DataFrame {

    private Index columns;
    private DataFrame[] dataFrames;

    public VConcatDataFrame(Index columns, DataFrame... dataFrames) {
        this.columns = columns;
        this.dataFrames = dataFrames;
    }

    @Override
    public Index getColumns() {
        return columns;
    }

    @Override
    public Iterator<RowProxy> iterator() {
        return new Iterator<RowProxy>() {

            private final ArrayRowBuilder rowBuilder = new ArrayRowBuilder(columns);
            private final ArrayRowProxy rowProxy = new ArrayRowProxy(columns);
            int[] ordinals;
            private int pos = 0;
            private Iterator<RowProxy> it;
            private RowProxy next;

            {
                rewind();
            }

            private void rewind() {

                if (it != null && it.hasNext()) {
                    next = it.next();
                } else if (pos < dataFrames.length) {
                    it = dataFrames[pos].iterator();
                    ordinals = buildOridinals(dataFrames[pos].getColumns());
                    pos++;

                    rewind();
                } else {
                    next = null;
                }
            }

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public RowProxy next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Past the end of the iterator");
                }

                RowProxy o = transform(next);
                rewind();
                return o;
            }

            private int[] buildOridinals(Index index) {
                int len = columns.size();
                int[] ordinals = new int[len];

                for (int i = 0; i < len; i++) {
                    String column = columns.getPositions()[i].name();
                    ordinals[i] = index.hasName(column) ? index.position(column).ordinal() : -1;
                }

                return ordinals;
            }

            private RowProxy transform(RowProxy row) {

                int len = ordinals.length;

                for (int i = 0; i < len; i++) {
                    if (ordinals[i] >= 0) {
                        rowBuilder.set(i, row.get(ordinals[i]));
                    }
                }

                return rowProxy.reset(rowBuilder.reset());
            }
        };
    }

    @Override
    public String toString() {
        return InlinePrinter.getInstance().print(new StringBuilder("VConcatDataFrame ["), this).append("]").toString();
    }
}
