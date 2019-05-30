package com.nhl.dflib.jdbc.connector.statement;

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @since 0.6
 */
public class CompiledFromStatementBinderFactory implements StatementBinderFactory {

    protected ValueConverterFactory converterFactory;

    public CompiledFromStatementBinderFactory(ValueConverterFactory converterFactory) {
        this.converterFactory = converterFactory;
    }

    @Override
    public StatementBinder createBinder(PreparedStatement statement) throws SQLException {

        ParameterMetaData pmd = statement.getParameterMetaData();
        int len = pmd.getParameterCount();
        StatementPosition[] positions = new StatementPosition[len];

        for (int i = 0; i < len; i++) {
            int jdbcPos = i + 1;
            int jdbcType = pmd.getParameterType(jdbcPos);
            positions[i] = new StatementPosition(statement, jdbcPos, jdbcType, converterFactory.findConverter(jdbcType));
        }

        return new StatementBinder(positions);
    }
}
