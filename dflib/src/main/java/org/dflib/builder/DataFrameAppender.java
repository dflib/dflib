package org.dflib.builder;

import org.dflib.DataFrame;

/**
 * Assembles a DataFrame from a sequence of objects of a some type. Supports source transformations, including
 * generation of primitive columns, etc.
 *
 * @since 0.16
 */
public class DataFrameAppender<S> {

    protected final RowAccum<S> rowAccum;

    protected DataFrameAppender(RowAccum<S> rowAccum) {
        this.rowAccum = rowAccum;
    }

    /**
     * Appends a single row, extracting data from the supplied object.
     */
    public DataFrameAppender<S> append(S rowSource) {
        rowAccum.push(rowSource);
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

    public DataFrame toDataFrame() {
        return rowAccum.toDataFrame();
    }
}
