package com.nhl.dflib.builder;

import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;

/**
 * Assembles a DataFrame from a sequence of objects of a some type. Supports source transformations, including
 * generation of primitive columns, etc.
 *
 * @since 0.16
 */
public class DataFrameAppender<S> {

    protected final Index columnsIndex;
    protected final SeriesBuilder<S, ?>[] columnBuilders;

    protected DataFrameAppender(Index columnsIndex, SeriesBuilder<S, ?>[] columnBuilders) {
        this.columnsIndex = columnsIndex;
        this.columnBuilders = columnBuilders;
    }

    /**
     * Appends a single row, extracting data from the supplied object.
     */
    public DataFrameAppender<S> append(S rowSource) {

        int w = columnBuilders.length;

        for (int i = 0; i < w; i++) {
            columnBuilders[i].extractAndStore(rowSource);
        }

        return this;
    }

    /**
     * Appends multiple rows from an iterable source.
     */
    public DataFrameAppender<S> append(Iterable<S> from) {

        for (S s : from) {
            append(s);
        }

        return this;
    }

    /**
     * Replaces a single existing row with new extracted row data
     */
    public DataFrameAppender<S> replace(S from, int toPos) {

        int w = columnBuilders.length;

        for (int i = 0; i < w; i++) {
            columnBuilders[i].extractAndStore(from, toPos);
        }

        return this;
    }

    public DataFrame build() {
        Series<?>[] series = new Series[columnBuilders.length];
        for (int i = 0; i < columnBuilders.length; i++) {
            series[i] = columnBuilders[i].toSeries();
        }

        return new ColumnDataFrame(columnsIndex, series);
    }
}
