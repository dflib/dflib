package com.nhl.dflib.jdbc.connector.statement;

import com.nhl.dflib.jdbc.connector.metadata.DbColumnMetadata;

import java.sql.PreparedStatement;

/**
 * @since 0.6
 */
public class FixedParamsBinderFactory implements StatementBinderFactory {

    protected ValueConverterFactory converterFactory;
    private DbColumnMetadata[] paramDescriptors;

    public FixedParamsBinderFactory(ValueConverterFactory converterFactory, DbColumnMetadata[] paramDescriptors) {
        this.converterFactory = converterFactory;
        this.paramDescriptors = paramDescriptors;
    }

    @Override
    public StatementBinder createBinder(PreparedStatement statement) {
        int len = paramDescriptors.length;
        ColumnBinder[] columnBinders = new ColumnBinder[len];

        for (int i = 0; i < len; i++) {
            int jdbcPos = i + 1;
            int jdbcType = paramDescriptors[i].getType();
            columnBinders[i] = new DefaultColumnBinder(statement, jdbcPos, jdbcType, converterFactory.findConverter(jdbcType));
        }

        return new StatementBinder(columnBinders);
    }
}
