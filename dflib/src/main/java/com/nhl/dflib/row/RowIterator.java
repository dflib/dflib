package com.nhl.dflib.row;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;

import java.util.Iterator;

public class RowIterator {

    public static Iterator<RowProxy> overDataFrame(DataFrame df) {
        return overArrays(df.getColumns(), df);
    }

    public static Iterator<RowProxy> overArrays(Index index, Iterable<Object[]> iterable) {

        return new Iterator<RowProxy>() {

            Iterator<Object[]> it = iterable.iterator();
            ArrayRowProxy rowProxy = new ArrayRowProxy(index);

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public RowProxy next() {
                return rowProxy.reset(it.next());
            }
        };
    }
}
