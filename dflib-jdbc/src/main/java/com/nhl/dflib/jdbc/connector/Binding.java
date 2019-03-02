package com.nhl.dflib.jdbc.connector;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.UnaryOperator;

public class Binding {

    private Object value;
    private int type;
    private int position;

    public Binding(int type, int position, Object value) {
        this.value = value;
        this.type = type;
        this.position = position;
    }

    public Binding mapValue(UnaryOperator<Object> valueMapper) {
        return new Binding(type, position, valueMapper.apply(value));
    }

    public void bind(PreparedStatement statement) throws SQLException {
        if (value == null) {
            statement.setNull(position, type);
        } else {
            statement.setObject(position, value, type);
        }
    }
}
