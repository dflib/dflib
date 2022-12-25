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

    public DataFrameArrayAppenderBuilder(Extractor<Object[], ?>... extractors) {
        super(extractors);
    }

    @Override
    public DataFrameArrayAppenderBuilder capacity(int capacity) {
        super.capacity(capacity);
        return this;
    }

    @Override
    public DataFrameArrayAppenderBuilder columnNames(String... columnNames) {
        super.columnNames(columnNames);
        return this;
    }

    @Override
    public DataFrameArrayAppenderBuilder columnIndex(Index columnsIndex) {
        super.columnIndex(columnsIndex);
        return this;
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
        Index index = columnsIndex();
        SeriesBuilder<Object[], ?>[] builders = builders(index);

        return rowSampleSize > 0
                ? new DataFrameArraySamplingAppender(index, builders, rowSampleSize, sampleRandom())
                : new DataFrameArrayAppender(index, builders);
    }
}
