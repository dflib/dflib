package com.nhl.dflib.builder;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Extractor;
import com.nhl.dflib.Index;
import com.nhl.dflib.sample.Sampler;

import java.util.Objects;
import java.util.Random;

/**
 * Builds a DataFrameExtractor that can be used to create DataFrames from a sequence of objects of a some type.
 *
 * @since 0.16
 */
public class DataFrameByRowBuilder<S> {

    private final Extractor<S, ?>[] columnsExtractors;

    private Index columnsIndex;
    private int capacity;
    protected int rowSampleSize;
    private Random rowsSampleRandom;

    @SafeVarargs
    public DataFrameByRowBuilder(Extractor<S, ?>... extractors) {
        this.columnsExtractors = Objects.requireNonNull(extractors);
    }

    public DataFrameByRowBuilder<S> columnNames(String... columnNames) {
        this.columnsIndex = Index.forLabels(columnNames);
        return this;
    }

    public DataFrameByRowBuilder<S> columnIndex(Index columnsIndex) {
        this.columnsIndex = columnsIndex;
        return this;
    }

    public DataFrameByRowBuilder<S> capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    /**
     * Configures the appender to select a sample of the rows from the source. Unlike
     * {@link DataFrame#sampleRows(int, Random)}, this method can be used on potentially very large data sets. If you
     * are executing multiple sampling runs in parallel, consider using {@link #sampleRows(int, Random)}, as this
     * method is using a shared {@link Random} instance with synchronization.
     *
     * @param size the size of the sample. Can be bigger than the result set size (as the result set size is not known
     *             upfront).
     */
    public DataFrameByRowBuilder<S> sampleRows(int size) {
        return sampleRows(size, Sampler.getDefaultRandom());
    }

    /**
     * Configures the appender to select a sample of the rows from the source. Unlike
     * {@link DataFrame#sampleRows(int, Random)}, this method can be used on potentially very large data sets.
     *
     * @param size   the size of the sample. Can be bigger than the result set size (as the result set size is not known upfront).
     * @param random a custom random number generator
     */
    public DataFrameByRowBuilder<S> sampleRows(int size, Random random) {
        this.rowSampleSize = size;
        this.rowsSampleRandom = random;
        return this;
    }

    /**
     * Creates an "appender" with this builder parameters. The appender can be used to build the DataFrame row by row.
     */
    public DataFrameAppender<S> appendData() {
        Index index = columnsIndex();
        SeriesBuilder<S, ?>[] builders = builders(index);

        return rowSampleSize > 0
                ? new DataFrameSamplingAppender<>(index, builders, rowSampleSize, sampleRandom())
                : new DataFrameAppender<>(index, builders);
    }

    public DataFrame build(Iterable<S> sources) {
        return appendData().append(sources).build();
    }

    protected int capacity() {
        return capacity > 0
                ? capacity
                : (rowSampleSize > 0 ? rowSampleSize : 10);
    }

    protected Index columnsIndex() {
        if (this.columnsIndex != null) {
            return this.columnsIndex;
        }

        int w = width();
        String[] labels = new String[w];
        for (int i = 0; i < w; i++) {
            labels[i] = Integer.toString(i);
        }

        return Index.forLabels(labels);
    }

    protected int width() {
        return columnsExtractors.length;
    }

    protected SeriesBuilder<S, ?>[] builders(Index columnsIndex) {
        int w = columnsIndex.size();
        int cw = columnsExtractors.length;
        SeriesBuilder<S, ?>[] builders = new SeriesBuilder[w];

        if (cw != w) {
            throw new IllegalArgumentException("Mismatch between the number of extractors and index width - " + cw + " vs " + w);
        }

        int capacity = capacity();
        for (int i = 0; i < w; i++) {
            builders[i] = new SeriesBuilder<>(columnsExtractors[i], capacity);
        }

        return builders;
    }

    protected Random sampleRandom() {
        return this.rowsSampleRandom != null ? this.rowsSampleRandom : Sampler.getDefaultRandom();
    }
}
