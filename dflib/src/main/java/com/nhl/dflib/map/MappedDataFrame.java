package com.nhl.dflib.map;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.print.InlinePrinter;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.row.ArrayRowBuilder;
import com.nhl.dflib.row.RowIterator;

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
    public int height() {
        return source.height();
    }

    @Override
    public Index getColumns() {
        return columns;
    }

    @Override
    public Iterator<Object[]> iterator() {
        return new Iterator<Object[]>() {

            private final Iterator<RowProxy> source = RowIterator.overDataFrame(MappedDataFrame.this.source);
            private final ArrayRowBuilder target = new ArrayRowBuilder(columns);

            @Override
            public boolean hasNext() {
                return source.hasNext();
            }

            @Override
            public Object[] next() {
                RowProxy next = source.next();
                rowMapper.map(next, target);
                return target.reset();
            }
        };
    }

    @Override
    public String toString() {
        return InlinePrinter.getInstance().print(new StringBuilder("MappedDataFrame ["), this).append("]").toString();
    }
}
