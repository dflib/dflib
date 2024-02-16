package org.dflib.builder;

import org.dflib.Extractor;

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
        int capacity = guessCapacity(-1);

        RowAccum<Object[]> rowAccum = rowAccum(capacity);
        return new DataFrameArrayAppender(rowAccum);
    }
}
