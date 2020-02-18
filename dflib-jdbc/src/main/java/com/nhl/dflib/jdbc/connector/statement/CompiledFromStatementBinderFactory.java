package com.nhl.dflib.jdbc.connector.statement;

import com.nhl.dflib.jdbc.connector.metadata.flavors.DbFlavor;

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @since 0.6
 */
public class CompiledFromStatementBinderFactory implements StatementBinderFactory {

    protected DbFlavor flavor;
    protected ValueConverterFactory converterFactory;

    public CompiledFromStatementBinderFactory(DbFlavor flavor, ValueConverterFactory converterFactory) {
        this.flavor = flavor;
        this.converterFactory = converterFactory;
    }

    @Override
    public StatementBinder createBinder(PreparedStatement statement) throws SQLException {

        ParameterMetaData pmd = statement.getParameterMetaData();
        int len = pmd.getParameterCount();
        StatementPosition[] positions = new StatementPosition[len];

        for (int i = 0; i < len; i++) {
            int jdbcPos = i + 1;
            int jdbcType = flavor.columnType(pmd.getParameterType(jdbcPos), pmd.getParameterTypeName(jdbcPos));
            positions[i] = new StatementPosition(statement, jdbcPos, jdbcType, converterFactory.findConverter(jdbcType));
        }

        return new StatementBinder(positions);
    }
}
