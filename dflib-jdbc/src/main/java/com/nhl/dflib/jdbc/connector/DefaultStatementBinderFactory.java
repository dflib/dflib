package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.jdbc.connector.metadata.DbColumnMetadata;
import com.nhl.dflib.jdbc.connector.statement.StatementBinder;
import com.nhl.dflib.jdbc.connector.statement.StatementBinderFactory;
import com.nhl.dflib.jdbc.connector.statement.StatementPosition;

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.function.Function;

/**
 * @since 0.6
 */
public class DefaultStatementBinderFactory implements StatementBinderFactory {

    private Function<Object, Object> defaultPreBindConverter;
    private Map<Integer, Function<Object, Object>> preBindConverters;

    public DefaultStatementBinderFactory(
            Function<Object, Object> defaultPreBindConverter,
            Map<Integer, Function<Object, Object>> preBindConverters) {

        this.defaultPreBindConverter = defaultPreBindConverter;
        this.preBindConverters = preBindConverters;
    }

    @Override
    public StatementBinder createBinder(PreparedStatement statement) throws SQLException {

        ParameterMetaData pmd = statement.getParameterMetaData();
        int len = pmd.getParameterCount();
        StatementPosition[] positions = new StatementPosition[len];

        for (int i = 0; i < len; i++) {
            int jdbcPos = i + 1;
            int jdbcType = pmd.getParameterType(jdbcPos);

            // TODO: are we missing on an opportunity to do specialized bindings like "st.setInt()", etc.?
            positions[i] = new StatementPosition(statement, jdbcPos, jdbcType, findConverter(jdbcType));
        }

        return new StatementBinder(positions);
    }

    @Override
    public StatementBinderFactory withFixedParams(DbColumnMetadata[] params) {
        return new FixedParamsStatementBinderFactory(defaultPreBindConverter, preBindConverters, params);
    }

    protected Function<Object, Object> findConverter(int jdbcType) {
        return preBindConverters.getOrDefault(jdbcType, defaultPreBindConverter);
    }
}
