package com.nhl.dflib.series;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.map.RowToValueMapper;
import com.nhl.dflib.row.RowProxy;

public class RowMappedSeries<T> implements Series<T> {

    private DataFrame source;
    private RowToValueMapper<T> mapper;
    private Series<T> materialized;

    public RowMappedSeries(DataFrame source, RowToValueMapper<T> mapper) {
        this.source = source;
        this.mapper = mapper;
    }

    @Override
    public int size() {
        return source.height();
    }

    @Override
    public T get(int index) {
        return getMaterialized().get(index);
    }

    protected Series<T> getMaterialized() {
        if (materialized == null) {
            synchronized (this) {
                if (materialized == null) {
                    materialized = doMaterialize();
                }
            }
        }

        return materialized;
    }

    protected Series<T> doMaterialize() {
        Object[] data = new Object[source.height()];

        int i = 0;
        for (RowProxy row : source) {
            data[i++] = mapper.map(row);
        }

        // reset source reference, allowing to free up memory..
        source = null;
        mapper = null;

        return new ArraySeries(data);
    }
}
