package com.nhl.dflib.map;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.print.InlinePrinter;
import com.nhl.dflib.row.ArrayRowBuilder;
import com.nhl.dflib.row.ArrayRowProxy;
import com.nhl.dflib.row.RowProxy;

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
    public Iterator<RowProxy> iterator() {
        return new Iterator<RowProxy>() {

            private final Iterator<RowProxy> it = source.iterator();
            private final ArrayRowBuilder rowBuilder = new ArrayRowBuilder(columns);
            private final ArrayRowProxy rowProxy = new ArrayRowProxy(columns);

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public RowProxy next() {
                RowProxy next = it.next();
                rowMapper.map(next, rowBuilder);
                return rowProxy.reset(rowBuilder.reset());
            }
        };
    }

    @Override
    public String toString() {
        return InlinePrinter.getInstance().print(new StringBuilder("MappedDataFrame ["), this).append("]").toString();
    }
}
