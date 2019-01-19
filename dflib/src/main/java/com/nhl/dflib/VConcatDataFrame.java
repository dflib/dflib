package com.nhl.dflib;

import com.nhl.dflib.print.InlinePrinter;

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
    public Iterator<Object[]> iterator() {
        return new Iterator<Object[]>() {

            int pos = 0;
            Iterator<Object[]> it;
            Object[] next;

            IndexPosition[] translator;

            {
                rewind();
            }

            private void rewind() {

                if (it != null && it.hasNext()) {
                    next = it.next();
                } else if (pos < dataFrames.length) {
                    it = dataFrames[pos].iterator();
                    translator = buildTranslator(dataFrames[pos].getColumns());
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
            public Object[] next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Past the end of the iterator");
                }

                Object[] o = transform(next);
                rewind();
                return o;
            }

            private IndexPosition[] buildTranslator(Index index) {
                int len = columns.size();
                IndexPosition[] translator = new IndexPosition[len];

                for (int i = 0; i < len; i++) {
                    String column = columns.getPositions()[i].name();
                    translator[i] = index.hasName(column) ? index.position(column) : null;
                }

                return translator;
            }

            private Object[] transform(Object[] row) {

                int len = translator.length;
                Object[] transformed = new Object[len];

                for (int i = 0; i < len; i++) {
                    transformed[i] = translator[i] != null ? translator[i].get(row) : null;
                }

                return transformed;
            }
        };
    }

    @Override
    public String toString() {
        return InlinePrinter.getInstance().print(new StringBuilder("VConcatDataFrame ["), this).append("]").toString();
    }
}
