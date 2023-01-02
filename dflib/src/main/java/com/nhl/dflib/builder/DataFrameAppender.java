package com.nhl.dflib.builder;

import com.nhl.dflib.DataFrame;

/**
 * Assembles a DataFrame from a sequence of objects of a some type. Supports source transformations, including
 * generation of primitive columns, etc.
 *
 * @since 0.16
 */
public class DataFrameAppender<S> {

    protected final DataFrameAppenderSink<S> sink;

    protected DataFrameAppender(DataFrameAppenderSink<S> sink) {
        this.sink = sink;
    }

    /**
     * Appends a single row, extracting data from the supplied object.
     */
    public DataFrameAppender<S> append(S rowSource) {
        sink.append(rowSource);
        return this;
    }

    /**
     * Appends multiple rows from an iterable source.
     */
    public DataFrameAppender<S> append(Iterable<S> from) {

        for (S s : from) {
            append(s);
        }

        return this;
    }

    /**
     * Replaces a single existing row with new extracted row data
     */
    public DataFrameAppender<S> replace(S from, int toPos) {
        sink.replace(from, toPos);
        return this;
    }

    public DataFrame toDataFrame() {
        return sink.toDataFrame();
    }
}
