package com.nhl.dflib.series;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.accumulator.ObjectAccumulator;
import com.nhl.dflib.row.RowProxy;

/**
 * A Series that is a concatenation of rows in a DataFrame.
 *
 * @since 0.6
 */
public class ByRowSeries extends ObjectSeries<Object> {

    private DataFrame source;
    private final int width;
    private final int size;

    private Series<Object> materialized;

    public ByRowSeries(DataFrame source) {
        // since we are concatenating multiple columns, the common type is Object.class
        super(Object.class);
        this.source = source;

        this.width = source.width();
        this.size = width * source.height();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Object get(int index) {

        // make a copy of "source" to avoid race conditions
        DataFrame source = this.source;
        if (source != null) {
            int row = index / width;
            int column = index % width;
            return source.getColumn(column).get(row);
        }

        return materialize().get(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        materialize().copyTo(to, fromOffset, toOffset, len);
    }

    @Override
    public Series<Object> materialize() {
        if (materialized == null) {
            synchronized (this) {
                if (materialized == null) {
                    materialized = doMaterialize();
                }
            }
        }

        return materialized;
    }

    protected Series<Object> doMaterialize() {
        ObjectAccumulator<Object> data = new ObjectAccumulator<>(size);

        for (RowProxy r : source) {
            for (int i = 0; i < width; i++) {
                data.add(r.get(i));
            }
        }

        // reset source reference, allowing to free up memory..
        source = null;

        return data.toSeries();
    }

    @Override
    public Series<Object> fillNulls(Object value) {
        return materialize().fillNulls(value);
    }

    @Override
    public Series<Object> fillNullsFromSeries(Series<?> values) {
        return materialize().fillNullsFromSeries(values);
    }

    @Override
    public Series<Object> fillNullsBackwards() {
        return materialize().fillNullsBackwards();
    }

    @Override
    public Series<Object> fillNullsForward() {
        return materialize().fillNullsForward();
    }
}
