package org.dflib.builder;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.Series;

class DefaultRowAccum<S> implements RowAccum<S> {

    protected final Index columnsIndex;
    protected final SeriesAppender<S, ?>[] columnBuilders;

    DefaultRowAccum(Index columnsIndex, SeriesAppender<S, ?>[] columnBuilders) {
        this.columnsIndex = columnsIndex;
        this.columnBuilders = columnBuilders;
    }

    @Override
    public void push(S rowSource) {
        int w = columnBuilders.length;

        for (int i = 0; i < w; i++) {
            columnBuilders[i].append(rowSource);
        }
    }

    @Override
    public void replace(int toPos, S rowSource) {
        int w = columnBuilders.length;

        for (int i = 0; i < w; i++) {
            columnBuilders[i].replace(rowSource, toPos);
        }
    }

    @Override
    public DataFrame toDataFrame() {
        Series<?>[] series = new Series[columnBuilders.length];
        for (int i = 0; i < columnBuilders.length; i++) {
            series[i] = columnBuilders[i].toSeries();
        }

        return new ColumnDataFrame(null, columnsIndex, series);
    }
}
