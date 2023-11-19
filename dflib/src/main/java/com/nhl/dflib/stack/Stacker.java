package com.nhl.dflib.stack;

import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.concat.SeriesConcat;
import com.nhl.dflib.series.IndexedSeries;
import com.nhl.dflib.series.IntSequenceSeries;
import com.nhl.dflib.series.SingleValueSeries;

public class Stacker {

    private static final String ROW_LABEL = "row";
    private static final String COLUMN_LABEL = "column";
    private static final String VALUE_LABEL = "value";

    public static DataFrame stackExcludeNulls(DataFrame df) {
        Index columnsIndex = df.getColumnsIndex();
        int w = columnsIndex.size();

        IntSeries[] rows = new IntSeries[w];
        Series<String>[] columns = new Series[w];
        Series<Object>[] values = new Series[w];

        for (int i = 0; i < w; i++) {
            rows[i] = df.getColumn(i).index(v -> v != null);
            columns[i] = new SingleValueSeries<>(columnsIndex.getLabel(i), rows[i].size());
            values[i] = new IndexedSeries<>(df.getColumn(i), rows[i]);
        }

        return new ColumnDataFrame(null, Index.of(ROW_LABEL, COLUMN_LABEL, VALUE_LABEL),
                SeriesConcat.intConcat(rows),
                SeriesConcat.concat(columns),
                SeriesConcat.concat(values)
        );
    }

    public static DataFrame stackIncludeNulls(DataFrame df) {
        Index columnsIndex = df.getColumnsIndex();
        int w = columnsIndex.size();
        int h = df.height();
        IntSeries sequence = new IntSequenceSeries(0, h);

        IntSeries[] rows = new IntSeries[w];
        Series<String>[] columns = new Series[w];
        Series<Object>[] values = new Series[w];

        for (int i = 0; i < w; i++) {
            rows[i] = sequence;
            columns[i] = new SingleValueSeries<>(columnsIndex.getLabel(i), h);
            values[i] = df.getColumn(i);
        }

        return new ColumnDataFrame(null, Index.of(ROW_LABEL, COLUMN_LABEL, VALUE_LABEL),
                SeriesConcat.intConcat(rows),
                SeriesConcat.concat(columns),
                SeriesConcat.concat(values)
        );
    }
}
