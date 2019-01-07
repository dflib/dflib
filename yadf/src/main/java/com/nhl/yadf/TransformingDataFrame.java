package com.nhl.yadf;

import com.nhl.yadf.map.DataRowMapper;
import com.nhl.yadf.map.MapContext;
import com.nhl.yadf.print.InlinePrinter;

import java.util.Iterator;
import java.util.Objects;

/**
 * A DataFrame over an Iterable of unknown (possibly very long) length. Its per-row operations are not applied
 * immediately and are instead deferred until the caller iterates over the contents.
 */
public class TransformingDataFrame implements DataFrame {

    private DataFrame source;
    private Index columns;
    private DataRowMapper rowMapper;

    protected TransformingDataFrame(Index columns, DataFrame source, DataRowMapper rowMapper) {
        this.source = Objects.requireNonNull(source);
        this.columns = Objects.requireNonNull(columns);
        this.rowMapper = Objects.requireNonNull(rowMapper);
    }

    @Override
    public long count() {
        return source.count();
    }

    @Override
    public Index getColumns() {
        return columns;
    }

    @Override
    public Iterator<Object[]> iterator() {
        return new Iterator<Object[]>() {

            private final Iterator<Object[]> delegateIt = TransformingDataFrame.this.source.iterator();
            private final MapContext context = new MapContext(source.getColumns(), columns);

            @Override
            public boolean hasNext() {
                return delegateIt.hasNext();
            }

            @Override
            public Object[] next() {
                return rowMapper.map(context, delegateIt.next());
            }
        };
    }

    @Override
    public String toString() {
        return InlinePrinter.getInstance().print(new StringBuilder("TransformingDataFrame ["), this).append("]").toString();
    }
}
