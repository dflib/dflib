package com.nhl.dflib.jdbc.select;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Binding {

    static final int NO_TYPE = Integer.MIN_VALUE;

    private Object value;
    private int type;

    public Binding(Object value) {
        this(value, NO_TYPE);
    }

    public Binding(Object value, int type) {
        this.value = value;
        this.type = type;
    }

    protected boolean typeUnknown() {
        return type == NO_TYPE;
    }

    public void bind(PreparedStatement statement, int position) {

        try {
            if (value == null) {
                bindNull(statement, position);
            } else {
                bindNotNull(statement, position, value);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error binding value at position '" + position + "'", e);
        }
    }

    protected void bindNull(PreparedStatement statement, int position) throws SQLException {

        int jdbcPosition = position + 1;
        int type = this.type;

        if (typeUnknown()) {
            type = statement.getParameterMetaData().getParameterType(jdbcPosition);
        }

        statement.setNull(jdbcPosition, type);
    }

    protected void bindNotNull(PreparedStatement statement, int position, Object value) throws SQLException {

        int jdbcPosition = position + 1;

        if (typeUnknown()) {
            statement.setObject(jdbcPosition, value);
        } else {
            statement.setObject(jdbcPosition, value, type);
        }
    }
}
