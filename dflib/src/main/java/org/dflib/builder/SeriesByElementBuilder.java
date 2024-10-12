package org.dflib.builder;

import org.dflib.Extractor;

import java.util.Collection;
import java.util.Objects;

// TODO: support for sampling and other customizations from DataFrameByRowBuilder
public class SeriesByElementBuilder<S, T> {

    private final Extractor<S, T> extractor;
    private int capacity;

    public SeriesByElementBuilder(Extractor<S, T> extractor) {
        this.extractor = Objects.requireNonNull(extractor);
    }

    public SeriesByElementBuilder<S, T> capacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public SeriesByElementBuilder<S, T> guessCapacity(Iterable<S> source) {
        this.capacity = (source instanceof Collection) ? ((Collection) source).size() : 10;
        return this;
    }

    /**
     * Creates an "appender" with this builder parameters. The appender can be used to build the DataFrame row by row.
     */
    public SeriesAppender<S, T> appender() {
        return new SeriesAppender<>(extractor, capacity());
    }

    protected int capacity() {
        return capacity > 0 ? capacity : 10;
    }
}
