package org.dflib.jdbc.connector.statement;

import org.dflib.jdbc.connector.metadata.flavors.DbFlavor;

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
        ColumnBinder[] columnBinders = new ColumnBinder[len];

        for (int i = 0; i < len; i++) {

            int jdbcPos = i + 1;

            if (flavor.supportsParamsMetadata()) {
                int jdbcType = flavor.columnType(pmd.getParameterType(jdbcPos), pmd.getParameterTypeName(jdbcPos));
                ValueConverter converter = converterFactory.findConverter(jdbcType);
                columnBinders[i] = new DefaultColumnBinder(statement, jdbcPos, jdbcType, converter);
            } else {
                // this is an iffy strategy, though seems to match MySQL expectations specifically
                columnBinders[i] = new ByJavaTypeColumnBinder(statement, jdbcPos);
            }
        }

        return new StatementBinder(columnBinders);
    }
}
