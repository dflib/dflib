package org.dflib.jdbc.connector.statement;

import java.sql.*;
import java.util.Calendar;

public class DefaultColumnBinder implements ColumnBinder {

    private final PreparedStatement statement;
    private final int type;
    private final int position;
    private final ValueConverter valueConverter;

    public DefaultColumnBinder(
            PreparedStatement statement,
            int position,
            int type,
            ValueConverter valueConverter) {

        this.statement = statement;
        this.type = type;
        this.position = position;
        this.valueConverter = valueConverter;
    }

    public void bind(Object o) throws SQLException {
        Object boundable = o != null ? valueConverter.convert(o) : null;

        if (boundable == null) {
            statement.setNull(position, type);
        } else if (boundable instanceof byte[]) {
            statement.setBytes(position, (byte[]) boundable);
        } else if (boundable instanceof Timestamp && type == Types.TIMESTAMP) {
            statement.setTimestamp(position, (Timestamp) boundable, Calendar.getInstance());
        } else if (boundable instanceof Time && type == Types.TIME) {
            statement.setTime(position, (Time) boundable, Calendar.getInstance());
        } else {
            statement.setObject(position, boundable, type);
        }
    }
}
