package com.nhl.dflib.builder;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;

import java.util.Random;

/**
 * A {@link DataFrameAppender} that does on-the-fly "sampling" of each appended row, potentially skipping a lot of rows.
 *
 * @since 0.16
 */
public class DataFrameSamplingAppender<S> extends DataFrameAppender<S> {

    private final AppenderSampler<S> sampler;

    public DataFrameSamplingAppender(Index columnsIndex, SeriesAppender<S, ?>[] columnBuilders, int rowSampleSize, Random rowsSampleRandom) {
        super(columnsIndex, columnBuilders);
        this.sampler = new AppenderSampler<>(rowSampleSize, rowsSampleRandom, super::append, super::replace);
    }

    @Override
    public DataFrameSamplingAppender<S> append(S rowSource) {
        sampler.append(rowSource);
        return this;
    }

    @Override
    public DataFrame toDataFrame() {
        return sampler.sortSampled(super.toDataFrame());
    }
}
