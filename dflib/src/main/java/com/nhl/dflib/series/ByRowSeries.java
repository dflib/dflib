package com.nhl.dflib.series;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.seriesbuilder.ObjectAccumulator;
import com.nhl.dflib.row.RowProxy;

/**
 * A Series that is a concatention of rows in a DataFrame.
 *
 * @since 0.6
 */
public class ByRowSeries<T> extends ObjectSeries<T> {

    private DataFrame source;
    private int width;
    private int height;
    private int size;

    private Series<T> materialized;

    public ByRowSeries(DataFrame source) {
        this.source = source;

        this.width = source.width();
        this.height = source.height();
        this.size = width * height;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public T get(int index) {

        // make a copy of "source" to avoid race conditions
        DataFrame source = this.source;
        if (source != null) {
            int row = index / width;
            int column = index % width;
            return (T) source.getColumn(column).get(row);
        }

        return materialize().get(index);
    }

    @Override
    public void copyTo(Object[] to, int fromOffset, int toOffset, int len) {
        materialize().copyTo(to, fromOffset, toOffset, len);
    }

    @Override
    public Series<T> materialize() {
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
        ObjectAccumulator data = new ObjectAccumulator(size);

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
    public Series<T> fillNulls(T value) {
        return materialize().fillNulls(value);
    }

    @Override
    public Series<T> fillNullsFromSeries(Series<? extends T> values) {
        return materialize().fillNullsFromSeries(values);
    }

    @Override
    public Series<T> fillNullsBackwards() {
        return materialize().fillNullsBackwards();
    }

    @Override
    public Series<T> fillNullsForward() {
        return materialize().fillNullsForward();
    }
}
