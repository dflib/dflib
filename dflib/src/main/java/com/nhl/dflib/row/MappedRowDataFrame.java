package com.nhl.dflib.row;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.map.RowMapper;

import java.util.Iterator;
import java.util.Objects;

/**
 * A DataFrame over an Iterable of unknown (possibly very long) length. Its per-row operations are not applied
 * immediately and are instead deferred until the caller iterates over the contents.
 */
public class MappedRowDataFrame extends BaseRowDataFrame {

    private DataFrame source;
    private RowMapper rowMapper;

    public MappedRowDataFrame(Index columns, DataFrame source, RowMapper rowMapper) {
        super(columns);
        this.source = Objects.requireNonNull(source);
        this.rowMapper = Objects.requireNonNull(rowMapper);
    }

    @Override
    public int height() {
        return source.height();
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
}
