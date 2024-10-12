package org.dflib.builder;

import org.dflib.Extractor;

/**
 * An appender builder with Object[] source that creates an appender with vararg support.

 */
public class DataFrameArrayByRowBuilder extends DataFrameByRowBuilder<Object[], DataFrameArrayByRowBuilder> {

    public DataFrameArrayByRowBuilder(Extractor<Object[], ?>... extractors) {
        super(extractors);
    }

    // override to return covariant subclass
    @Override
    public DataFrameArrayAppender appender() {
        return appender(guessCapacity(-1));
    }

    @Override
    protected DataFrameArrayAppender appender(int capacity) {
        RowAccum<Object[]> rowAccum = rowAccum(capacity);
        return new DataFrameArrayAppender(rowAccum);
    }
}
