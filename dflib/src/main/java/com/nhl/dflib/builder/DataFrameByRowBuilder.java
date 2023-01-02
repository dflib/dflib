package com.nhl.dflib.builder;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Extractor;
import com.nhl.dflib.Index;
import com.nhl.dflib.sample.Sampler;

import java.util.Collection;
import java.util.Objects;
import java.util.Random;

/**
 * Builds a DataFrameExtractor that can be used to create DataFrames from a sequence of objects of a some type.
 *
 * @since 0.16
 */
public class DataFrameByRowBuilder<S> {

    static final int DEFAULT_CAPACITY = 10;

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
        return columnIndex(Index.forLabels(columnNames));
    }

    public DataFrameByRowBuilder<S> columnIndex(Index columnsIndex) {

        if (columnsIndex.size() != columnsExtractors.length) {
            throw new IllegalArgumentException("Column index size must match the size of the extractors ("
                    + columnsExtractors.length
                    + "): "
                    + columnsIndex.size());
        }

        this.columnsIndex = columnsIndex;
        return this;
    }

    public DataFrameByRowBuilder<S> capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public DataFrameByRowBuilder<S> guessCapacity(Iterable<S> source) {
        this.capacity = (source instanceof Collection) ? ((Collection) source).size() : DEFAULT_CAPACITY;
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
    public DataFrameAppender<S> appender() {
        RowAccum<S> rowAccum = rowAccum();
        return new DataFrameAppender<>(rowAccum);
    }

    public DataFrame ofIterable(Iterable<S> sources) {
        return appender().append(sources).toDataFrame();
    }

    protected RowAccum<S> rowAccum() {
        Index index = columnsIndex();
        SeriesAppender<S, ?>[] builders = builders(index);
        DefaultRowAccum<S> accumSink = new DefaultRowAccum<>(index, builders);

        return rowSampleSize > 0
                ? new SamplingRowAccum<>(accumSink, rowSampleSize, sampleRandom())
                : accumSink;
    }

    protected int capacity() {
        return capacity > 0
                ? capacity
                : (rowSampleSize > 0 ? rowSampleSize : DEFAULT_CAPACITY);
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

    protected SeriesAppender<S, ?>[] builders(Index columnsIndex) {
        int w = columnsIndex.size();
        int cw = columnsExtractors.length;
        SeriesAppender<S, ?>[] builders = new SeriesAppender[w];

        if (cw != w) {
            throw new IllegalArgumentException("Mismatch between the number of extractors and index width - " + cw + " vs " + w);
        }

        int capacity = capacity();
        for (int i = 0; i < w; i++) {
            builders[i] = new SeriesAppender<>(columnsExtractors[i], capacity);
        }

        return builders;
    }

    protected Random sampleRandom() {
        return this.rowsSampleRandom != null ? this.rowsSampleRandom : Sampler.getDefaultRandom();
    }
}
