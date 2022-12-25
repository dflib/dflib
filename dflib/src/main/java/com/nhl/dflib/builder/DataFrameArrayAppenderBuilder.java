package com.nhl.dflib.builder;

import com.nhl.dflib.Extractor;
import com.nhl.dflib.Index;

import java.util.Random;

/**
 * An appender builder with Object[] source that creates an appender with vararg support.
 *
 * @since 0.16
 */
public class DataFrameArrayAppenderBuilder extends DataFrameAppenderBuilder<Object[]> {

    public DataFrameArrayAppenderBuilder(Index columnsIndex, Extractor<Object[], ?>[] columnExtractors) {
        super(columnsIndex, columnExtractors);
    }

    @Override
    public DataFrameArrayAppenderBuilder sampleRows(int size) {
        super.sampleRows(size);
        return this;
    }

    @Override
    public DataFrameArrayAppenderBuilder sampleRows(int size, Random random) {
        super.sampleRows(size, random);
        return this;
    }

    @Override
    public DataFrameArrayAppender appendData() {
        return rowSampleSize > 0
                ? new DataFrameArraySamplingAppender(columnsIndex, builders(), rowSampleSize, sampleRandom())
                : new DataFrameArrayAppender(columnsIndex, builders());
    }
}
