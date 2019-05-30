package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.jdbc.connector.metadata.DbColumnMetadata;

import java.sql.PreparedStatement;
import java.util.Map;
import java.util.function.Function;

/**
 * @since 0.6
 */
public class FixedParamsStatementBinderFactory extends DefaultStatementBinderFactory {

    private DbColumnMetadata[] params;

    public FixedParamsStatementBinderFactory(
            Function<Object, Object> defaultPreBindConverter,
            Map<Integer, Function<Object, Object>> preBindConverters,
            DbColumnMetadata[] params) {

        super(defaultPreBindConverter, preBindConverters);
        this.params = params;
    }

    @Override
    public StatementBinder createBinder(PreparedStatement statement) {
        int len = params.length;
        StatementPosition[] positions = new StatementPosition[len];

        for (int i = 0; i < len; i++) {
            int jdbcPos = i + 1;
            int jdbcType = params[i].getType();

            // TODO: are we missing on an opportunity to do specialized bindings like "st.setInt()", etc.?
            positions[i] = new StatementPosition(statement, jdbcPos, jdbcType, findConverter(jdbcType));
        }

        return new StatementBinder(positions);
    }
}
