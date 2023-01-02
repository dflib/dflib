package com.nhl.dflib.builder;

import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;

/**
 * @since 0.16
 */
class DataFrameAccumSink<S> implements DataFrameAppenderSink<S> {

    protected final Index columnsIndex;
    protected final SeriesAppender<S, ?>[] columnBuilders;

    DataFrameAccumSink(Index columnsIndex, SeriesAppender<S, ?>[] columnBuilders) {
        this.columnsIndex = columnsIndex;
        this.columnBuilders = columnBuilders;
    }

    @Override
    public void append(S rowSource) {
        int w = columnBuilders.length;

        for (int i = 0; i < w; i++) {
            columnBuilders[i].append(rowSource);
        }
    }

    @Override
    public void replace(S from, int toPos) {
        int w = columnBuilders.length;

        for (int i = 0; i < w; i++) {
            columnBuilders[i].replace(from, toPos);
        }
    }

    @Override
    public DataFrame toDataFrame() {
        Series<?>[] series = new Series[columnBuilders.length];
        for (int i = 0; i < columnBuilders.length; i++) {
            series[i] = columnBuilders[i].toSeries();
        }

        return new ColumnDataFrame(columnsIndex, series);
    }
}
