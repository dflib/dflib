package com.nhl.dflib.jdbc.connector.loader;

import com.nhl.dflib.loader.SeriesBuilder;
import com.nhl.dflib.loader.ValueExtractor;

import java.sql.ResultSet;

/**
 * A {@link SeriesBuilder} reading values from JDBC {@link ResultSet}.
 *
 * @since 0.16
 */
public class JdbcSeriesBuilder<T> extends SeriesBuilder<ResultSet, T> {

    public JdbcSeriesBuilder(ValueExtractor<ResultSet, T> converter) {
        super(converter);
    }
}
