package org.dflib.jdbc.connector.statement;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;

/**
 * A ColumnBinder for DBs that don't provide explicit PreparedStatement parameter metadata. MySQL is one of the DBs this
 * binder is applicable for. Luckily it allows binding by Java type regardless of the underlying column.
 */
public class ByJavaTypeColumnBinder implements ColumnBinder {

    // MySQL returns VARCHAR as the fake type for all parameters, and seems to allow binding most values as such
    private static final int FAKE_JDBC_TYPE = Types.VARCHAR;

    private final PreparedStatement statement;
    private final int position;

    public ByJavaTypeColumnBinder(PreparedStatement statement, int position) {
        this.statement = statement;
        this.position = position;
    }

    @Override
    public void bind(Object o) throws SQLException {

        Object boundable = normalize(o);

        if (boundable == null) {
            statement.setNull(position, FAKE_JDBC_TYPE);
        } else if (boundable instanceof byte[]) {
            statement.setBytes(position, (byte[]) boundable);
        }
        // MySQL 8 requires a Calendar instance to save local time without undesired TZ conversion.
        // Other DBs work fine with or without the calendar
        else if (boundable instanceof Timestamp) {
            statement.setTimestamp(position, (Timestamp) boundable, Calendar.getInstance());
        }
        // MySQL 8 requires a Calendar instance to save local time without undesired TZ conversion.
        // Other DBs work fine with or without the calendar
        else if (boundable instanceof Time) {
            statement.setTime(position, (Time) boundable, Calendar.getInstance());
        } else {
            statement.setObject(position, boundable, FAKE_JDBC_TYPE);
        }
    }

    private Object normalize(Object o) {
        if (o == null) {
            return o;
        }

        // TODO: something similar to ValueConverter, so that we don't need to scan through known types linearly
        if (o instanceof LocalDateTime) {
            return Timestamp.valueOf((LocalDateTime) o);
        }

        if (o instanceof LocalTime) {
            return Time.valueOf((LocalTime) o);
        }

        if (o instanceof LocalDate) {
            return Date.valueOf((LocalDate) o);
        }

        return o;
    }
}
