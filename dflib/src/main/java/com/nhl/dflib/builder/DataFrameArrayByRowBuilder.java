package com.nhl.dflib.builder;

import com.nhl.dflib.Extractor;

/**
 * An appender builder with Object[] source that creates an appender with vararg support.
 *
 * @since 0.16
 */
public class DataFrameArrayByRowBuilder extends DataFrameByRowBuilder<Object[], DataFrameArrayByRowBuilder> {

    public DataFrameArrayByRowBuilder(Extractor<Object[], ?>... extractors) {
        super(extractors);
    }

    @Override
    public DataFrameArrayAppender appender() {
        RowAccum<Object[]> rowAccum = rowAccum();
        return new DataFrameArrayAppender(rowAccum);
    }
}
