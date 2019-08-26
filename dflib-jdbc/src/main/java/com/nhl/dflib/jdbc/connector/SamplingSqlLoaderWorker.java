package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.series.builder.IntAccumulator;
import com.nhl.dflib.series.builder.SeriesBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Random;

class SamplingSqlLoaderWorker extends SqlLoaderWorker {

    private int rowSampleSize;
    private Random rowsSampleRandom;
    private IntAccumulator sampledRows;

    public SamplingSqlLoaderWorker(
            Index columns,
            SeriesBuilder<ResultSet, ?>[] accumulators,
            int maxRows,
            int rowSampleSize,
            Random rowsSampleRandom) {

        super(columns, accumulators, maxRows);
        this.rowSampleSize = rowSampleSize;
        this.rowsSampleRandom = Objects.requireNonNull(rowsSampleRandom);
        this.sampledRows = new IntAccumulator();
    }

    @Override
    protected DataFrame toDataFrame() {
        return sortSampled(super.toDataFrame());
    }

    protected DataFrame sortSampled(DataFrame sampledUnsorted) {
        DataFrame index = DataFrame
                .newFrame("a")
                .columns(sampledRows.toIntSeries())
                .addRowNumber("b")
                .sort(0, true);

        return sampledUnsorted.selectRows(index.getColumnAsInt(1));
    }

    @Override
    protected void consumeResultSet(ResultSet rs) throws SQLException {
        int w = accumulators.length;
        int size = 0;
        int i = 0;

        while (rs.next() && size++ < maxRows) {
            sampleRow(i++, w, rs);
        }
    }

    protected void sampleRow(int rowNumber, int width, ResultSet rs) {

        // Reservoir sampling algorithm per https://en.wikipedia.org/wiki/Reservoir_sampling

        // fill "reservoir" first
        if (rowNumber < rowSampleSize) {
            addRow(width, rs);
            sampledRows.add(rowNumber);
        }
        // replace previously filled values based on random sampling with decaying probability
        else {
            int pos = rowsSampleRandom.nextInt(rowNumber + 1);
            if (pos < rowSampleSize) {
                replaceRow(pos, width, rs);
                sampledRows.set(pos, rowNumber);
            }
        }
    }

    protected void replaceRow(int pos, int width, ResultSet rs) {
        for (int i = 0; i < width; i++) {
            accumulators[i].set(pos, rs);
        }
    }
}
