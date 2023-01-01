package com.nhl.dflib.builder;

import com.nhl.dflib.Index;

/**
 * Assembles a DataFrame from a sequence of Object arrays. Supports source transformations, including generation of
 * primitive columns, etc.
 *
 * @since 0.16
 */
public class DataFrameArrayAppender extends DataFrameAppender<Object[]> {

    protected DataFrameArrayAppender(Index columnsIndex, SeriesAppender<Object[], ?>[] columnBuilders) {
        super(columnsIndex, columnBuilders);
    }

    /**
     * Appends a single row, extracting data from the supplied array vararg.
     */
    @Override
    public DataFrameArrayAppender append(Object... rowSource) {

        int w = columnBuilders.length;

        for (int i = 0; i < w; i++) {
            columnBuilders[i].append(rowSource);
        }

        return this;
    }
}
