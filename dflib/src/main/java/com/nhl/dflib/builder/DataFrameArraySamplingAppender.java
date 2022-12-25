package com.nhl.dflib.builder;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;

import java.util.Random;

/**
 * @since 0.16
 */
public class DataFrameArraySamplingAppender extends DataFrameArrayAppender {

    private final AppenderSampler<Object[]> sampler;

    public DataFrameArraySamplingAppender(
            Index columnsIndex,
            SeriesBuilder<Object[], ?>[] columnBuilders,
            int rowSampleSize,
            Random rowsSampleRandom) {

        super(columnsIndex, columnBuilders);
        this.sampler = new AppenderSampler<>(rowSampleSize, rowsSampleRandom, super::append, super::replace);
    }

    @Override
    public DataFrameArraySamplingAppender append(Object... rowSource) {
        sampler.append(rowSource);
        return this;
    }

    public DataFrame build() {
        return sampler.sortSampled(super.build());
    }
}
