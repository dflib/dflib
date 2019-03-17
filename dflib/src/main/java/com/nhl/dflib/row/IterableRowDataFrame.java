package com.nhl.dflib.row;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.print.InlinePrinter;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;

public class IterableRowDataFrame extends BaseRowDataFrame {

    private Iterable<Object[]> source;

    public IterableRowDataFrame(Index columns, Iterable<Object[]> source) {
        super(columns);
        this.source = source;
    }

    /**
     * Creates a DataFrame from an iterable over arbitrary objects. Each object will be converted to a row by applying
     * a function passed as the last argument.
     */
    public static <T> DataFrame fromObjects(Index columns, Iterable<T> rows, Function<T, Object[]> rowMapper) {
        return new IterableRowDataFrame(columns, new TransformingIterable<>(rows, rowMapper)).materialize();
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

    @Override
    public String toString() {
        return InlinePrinter.getInstance().print(new StringBuilder("IterableRowDataFrame ["), this).append("]").toString();
    }
}
