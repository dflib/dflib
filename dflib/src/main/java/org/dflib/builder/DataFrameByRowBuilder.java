package org.dflib.builder;

import org.dflib.DataFrame;
import org.dflib.Extractor;
import org.dflib.Index;
import org.dflib.RowPredicate;
import org.dflib.sample.Sampler;

import java.util.Collection;
import java.util.Objects;
import java.util.Random;

/**
 * Builds a DataFrameExtractor that can be used to create DataFrames from a sequence of objects of a some type.
 *
 * @since 0.16
 */
public class DataFrameByRowBuilder<S, B extends DataFrameByRowBuilder<S, B>> {

    static final int DEFAULT_CAPACITY = 1000;

    private final Extractor<S, ?>[] columnsExtractors;

    private Index columnsIndex;
    private int capacity;
    protected int rowSampleSize;
    private Random rowsSampleRandom;
    private RowPredicate rowFilter;

    @SafeVarargs
    public DataFrameByRowBuilder(Extractor<S, ?>... extractors) {
        this.columnsExtractors = Objects.requireNonNull(extractors);
    }

    public B columnNames(String... columnNames) {
        return columnIndex(Index.of(columnNames));
    }

    public B columnIndex(Index columnsIndex) {

        if (columnsIndex.size() != columnsExtractors.length) {
            throw new IllegalArgumentException("Column index size must match the size of the extractors ("
                    + columnsExtractors.length
                    + "): "
                    + columnsIndex.size());
        }

        this.columnsIndex = columnsIndex;
        return (B) this;
    }

    /**
     * Explicitly sets builder "vertical" capacity to avoid internal array resizing. If not set, DFLib will make a
     * guess if possible or use a default.
     */
    public B capacity(int capacity) {
        this.capacity = capacity;
        return (B) this;
    }

    public B selectRows(RowPredicate rowFilter) {
        this.rowFilter = rowFilter;
        return (B) this;
    }

    /**
     * Configures the appender to select a sample of the rows from the source. Unlike
     * {@link DataFrame#rowsSample(int, Random)}, this method can be used on potentially very large data sets. If you
     * are executing multiple sampling runs in parallel, consider using {@link #sampleRows(int, Random)}, as this
     * method is using a shared {@link Random} instance with synchronization.
     *
     * @param size the size of the sample. Can be bigger than the result set size (as the result set size is not known
     *             upfront).
     */
    public B sampleRows(int size) {
        return sampleRows(size, Sampler.getDefaultRandom());
    }

    /**
     * Configures the appender to select a sample of the rows from the source. Unlike
     * {@link DataFrame#rowsSample(int, Random)}, this method can be used on potentially very large data sets.
     *
     * @param size   the size of the sample. Can be bigger than the result set size (as the result set size is not known upfront).
     * @param random a custom random number generator
     */
    public B sampleRows(int size, Random random) {
        this.rowSampleSize = size;
        this.rowsSampleRandom = random;
        return (B) this;
    }

    public DataFrame ofIterable(Iterable<S> sources) {
        int autoCapacity = (sources instanceof Collection) ? ((Collection) sources).size() : -1;
        int capacity = guessCapacity(autoCapacity);

        return appender(capacity).append(sources).toDataFrame();
    }

    /**
     * Creates an "appender" with this builder parameters. The appender can be used to build the DataFrame row by row.
     */
    public DataFrameAppender<S> appender() {
        return appender(guessCapacity(-1));
    }

    protected DataFrameAppender<S> appender(int capacity) {
        RowAccum<S> rowAccum = rowAccum(capacity);
        return new DataFrameAppender<>(rowAccum);
    }

    protected RowAccum<S> rowAccum(int capacity) {
        Index index = columnsIndex();
        SeriesAppender<S, ?>[] builders = builders(index, capacity);
        RowAccum<S> accum = new DefaultRowAccum<>(index, builders);

        if (rowSampleSize > 0) {
            accum = new SamplingRowAccum<>(accum, rowSampleSize, sampleRandom());
        }

        if (rowFilter != null) {
            accum = new FilteringRowAccum<>(accum, rowFilter, index, columnsExtractors);
        }

        return accum;
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

        return Index.of(labels);
    }

    protected int width() {
        return columnsExtractors.length;
    }

    protected SeriesAppender<S, ?>[] builders(Index columnsIndex, int capacity) {
        int w = columnsIndex.size();
        int cw = columnsExtractors.length;
        SeriesAppender<S, ?>[] builders = new SeriesAppender[w];

        if (cw != w) {
            throw new IllegalArgumentException("Mismatch between the number of extractors and index width - " + cw + " vs " + w);
        }

        for (int i = 0; i < w; i++) {
            builders[i] = new SeriesAppender<>(columnsExtractors[i], capacity);
        }

        return builders;
    }

    protected Random sampleRandom() {
        return this.rowsSampleRandom != null ? this.rowsSampleRandom : Sampler.getDefaultRandom();
    }

    protected int guessCapacity(int guessedCapacity) {
        int defaultCapacity = guessedCapacity > 0 ? guessedCapacity : DEFAULT_CAPACITY;

        if (this.capacity > 0) {
            return this.capacity;
        } else if (rowSampleSize > 0) {
            return Math.min(rowSampleSize, defaultCapacity);
        } else {
            return defaultCapacity;
        }
    }
}
