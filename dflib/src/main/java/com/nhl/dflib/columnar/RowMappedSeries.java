package com.nhl.dflib.columnar;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.map.RowToValueMapper;
import com.nhl.dflib.row.RowProxy;

public class RowMappedSeries<T> implements Series<T> {

    private DataFrame dataFrame;
    private RowToValueMapper<T> mapper;
    private Series<T> materialized;

    public RowMappedSeries(DataFrame dataFrame, RowToValueMapper<T> mapper) {
        this.dataFrame = dataFrame;
        this.mapper = mapper;
    }

    @Override
    public int size() {
        return dataFrame.height();
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
        Object[] data = new Object[dataFrame.height()];

        int i = 0;
        for (RowProxy row : dataFrame) {
            data[i++] = mapper.map(row);
        }

        return new ArraySeries(data);
    }
}
