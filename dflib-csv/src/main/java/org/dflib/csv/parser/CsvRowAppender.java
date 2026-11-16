package org.dflib.csv.parser;

import org.dflib.DataFrame;
import org.dflib.Extractor;
import org.dflib.Index;
import org.dflib.RowPredicate;
import org.dflib.Series;
import org.dflib.builder.IntAccum;
import org.dflib.builder.ValueAccum;
import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.format.CsvParserConfig;
import org.dflib.row.ValueHolderRowProxy;
import org.dflib.sample.Sampler;

import java.util.Arrays;
import java.util.Random;

/**
 * Builds a DataFrame from CSV rows, filtering and sampling them per config. Unlike the core
 * {@link org.dflib.builder.DataFrameAppender}, can add columns mid-load.
 */
class CsvRowAppender {

    private static final int CAPACITY = 1000;

    private final RowPredicate filter;
    private final int sampleSize;
    private final Random sampleRandom;
    private final IntAccum sampledRows;

    private Index index;
    private Column<?>[] columns;
    private ValueHolderRowProxy<DataSlice[]> rowProxy;
    private int rowsRead;

    CsvRowAppender(Index index, Extractor<DataSlice[], ?>[] extractors, CsvParserConfig config) {
        this.filter = config.rowCondition();
        this.sampleSize = config.rowSampleSize();
        this.sampleRandom = config.rowsSampleRandom() != null ? config.rowsSampleRandom() : Sampler.getDefaultRandom();
        this.sampledRows = sampleSize > 0 ? new IntAccum() : null;

        int capacity = sampleSize > 0 ? Math.min(sampleSize, CAPACITY) : CAPACITY;
        this.columns = new Column[extractors.length];
        for (int i = 0; i < extractors.length; i++) {
            columns[i] = new Column<>(extractors[i], capacity, 0);
        }

        setIndex(index, extractors);
    }

    void append(DataSlice[] row) {
        if (filter != null && !test(row)) {
            return;
        }

        if (sampleSize > 0) {
            sample(row);
        } else {
            push(row);
        }
    }

    private boolean test(DataSlice[] row) {
        rowProxy.reset(row);
        return filter.test(rowProxy);
    }

    private void push(DataSlice[] row) {
        for (Column<?> c : columns) {
            c.append(row);
        }
    }

    /**
     * Reservoir sampling, same as in {@link org.dflib.builder.DataFrameByRowBuilder#sampleRows(int, Random)}.
     */
    private void sample(DataSlice[] row) {
        if (rowsRead < sampleSize) {
            push(row);
            sampledRows.pushInt(rowsRead);
        } else {
            int pos = sampleRandom.nextInt(rowsRead + 1);
            if (pos < sampleSize) {
                for (Column<?> c : columns) {
                    c.replace(row, pos);
                }
                sampledRows.replaceInt(pos, rowsRead);
            }
        }

        rowsRead++;
    }

    /**
     * Switches to wider columns that start with the current ones. The rows appended so far get nulls (or primitive
     * defaults) in the new columns.
     */
    void addColumns(Index index, Extractor<DataSlice[], ?>[] extractors) {
        int w = columns.length;
        int h = w > 0 ? columns[0].accum.size() : 0;

        this.columns = Arrays.copyOf(columns, extractors.length);
        for (int i = w; i < extractors.length; i++) {
            columns[i] = new Column<>(extractors[i], CAPACITY, h);
        }

        setIndex(index, extractors);
    }

    DataFrame toDataFrame() {
        Series<?>[] series = new Series[columns.length];
        for (int i = 0; i < columns.length; i++) {
            series[i] = columns[i].accum.toSeries();
        }

        DataFrame df = DataFrame.byColumn(index).of(series);
        return sampleSize > 0 ? df.rows(sampledRows.toSeries().sortIndexInt()).select() : df;
    }

    private void setIndex(Index index, Extractor<DataSlice[], ?>[] extractors) {
        this.index = index;
        this.rowProxy = filter != null ? new ValueHolderRowProxy<>(index, extractors) : null;
    }

    private static final class Column<T> {

        final Extractor<DataSlice[], T> extractor;
        final ValueAccum<T> accum;

        Column(Extractor<DataSlice[], T> extractor, int capacity, int nulls) {
            this.extractor = extractor;
            this.accum = extractor.createAccum(nulls + capacity);
            for (int i = 0; i < nulls; i++) {
                accum.push(null);
            }
        }

        void append(DataSlice[] row) {
            extractor.extractAndStore(row, accum);
        }

        void replace(DataSlice[] row, int pos) {
            extractor.extractAndStore(row, accum, pos);
        }
    }
}
