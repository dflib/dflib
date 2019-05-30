package com.nhl.dflib.jdbc.connector;

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @since 0.6
 */
public class StatementBinderFactory {

    private Function<Object, Object> defaultPreBindConverter;
    private Map<Integer, Function<Object, Object>> preBindConverters;

    public StatementBinderFactory() {
        this.defaultPreBindConverter = StatementPositionConverters.defaultConverter();
        this.preBindConverters = new HashMap<>();
        this.preBindConverters.put(Types.DATE, StatementPositionConverters.dateConverter());
        this.preBindConverters.put(Types.TIME, StatementPositionConverters.timeConverter());
        this.preBindConverters.put(Types.TIMESTAMP, StatementPositionConverters.timestampConverter());
        this.preBindConverters.put(Types.INTEGER, StatementPositionConverters.intConverter());
        this.preBindConverters.put(Types.VARCHAR, StatementPositionConverters.stringConverter());
    }

    public StatementBinder createBinder(PreparedStatement statement) throws SQLException {

        ParameterMetaData pmd = statement.getParameterMetaData();
        int len = pmd.getParameterCount();
        StatementPosition[] positions = new StatementPosition[len];

        for (int i = 0; i < len; i++) {
            int jdbcPos = i + 1;
            int jdbcType = pmd.getParameterType(jdbcPos);

            // TODO: are missing on opportunity to do specialized bindings like "st.setInt()", etc.?
            positions[i] = new StatementPosition(statement, jdbcPos, jdbcType, findConverter(jdbcType));
        }

        return new StatementBinder(positions);
    }

    private Function<Object, Object> findConverter(int jdbcType) {
        return preBindConverters.getOrDefault(jdbcType, defaultPreBindConverter);
    }
}
