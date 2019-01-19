package com.nhl.dflib.map;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.print.InlinePrinter;

import java.util.Iterator;
import java.util.Objects;

/**
 * A DataFrame over an Iterable of unknown (possibly very long) length. Its per-row operations are not applied
 * immediately and are instead deferred until the caller iterates over the contents.
 */
public class MappedDataFrame implements DataFrame {

    private DataFrame source;
    private Index columns;
    private RowMapper rowMapper;

    public MappedDataFrame(Index columns, DataFrame source, RowMapper rowMapper) {
        this.source = Objects.requireNonNull(source);
        this.columns = Objects.requireNonNull(columns);
        this.rowMapper = Objects.requireNonNull(rowMapper);
    }

    @Override
    public long height() {
        return source.height();
    }

    @Override
    public Index getColumns() {
        return columns;
    }

    @Override
    public Iterator<Object[]> iterator() {
        return new Iterator<Object[]>() {

            private final Iterator<Object[]> delegateIt = MappedDataFrame.this.source.iterator();
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
        return InlinePrinter.getInstance().print(new StringBuilder("MappedDataFrame ["), this).append("]").toString();
    }
}
