package com.nhl.dflib.jdbc.connector.statement;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatementPosition {

    private PreparedStatement statement;
    private int type;
    private int position;
    private ValueConverter valueConverter;

    public StatementPosition(
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
        } else {
            statement.setObject(position, boundable, type);
        }
    }
}
