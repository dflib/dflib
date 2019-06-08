package com.nhl.dflib;

import com.nhl.dflib.series.builder.ObjectAccumulator;
import com.nhl.dflib.series.builder.SeriesBuilder;

/**
 * Assembles a DataFrame row by row. Created indirectly inside {@link DataFrameBuilder#addRow(Object...)}.
 *
 * @since 0.6
 */
public class DataFrameByRowBuilder {

    private Index columnsIndex;
    private SeriesBuilder<Object, Object>[] columnBuilders;

    protected DataFrameByRowBuilder(Index columnsIndex) {
        this.columnsIndex = columnsIndex;

        int w = columnsIndex.size();
        this.columnBuilders = new SeriesBuilder[w];
        for (int i = 0; i < w; i++) {
            columnBuilders[i] = new ObjectAccumulator<>();
        }
    }

    public DataFrameByRowBuilder addRow(Object... row) {

        int w = columnBuilders.length;

        if (row.length < w) {
            throw new IllegalArgumentException("Row must be at least " + w + " elements long: " + row.length);
        }

        for (int i = 0; i < w; i++) {
            columnBuilders[i].add(row[i]);
        }

        return this;
    }

    public DataFrame create() {
        Series<?>[] series = new Series[columnBuilders.length];
        for (int i = 0; i < columnBuilders.length; i++) {
            series[i] = columnBuilders[i].toSeries();
        }

        return new ColumnDataFrame(columnsIndex, series);
    }
}
