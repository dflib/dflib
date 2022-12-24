package com.nhl.dflib.jdbc.connector.loader;

import com.nhl.dflib.accumulator.Accumulator;
import com.nhl.dflib.accumulator.ColumnBuilder;
import com.nhl.dflib.accumulator.ValueConverter;

import java.sql.ResultSet;

/**
 * A {@link ColumnBuilder} reading values from JDBC {@link ResultSet}.
 *
 * @since 0.16
 */
public class JdbcColumnBuilder<T> extends ColumnBuilder<ResultSet, T> {

    public JdbcColumnBuilder(ValueConverter<ResultSet, T> converter, Accumulator<T> accumulator) {
        super(converter, accumulator);
    }
}
