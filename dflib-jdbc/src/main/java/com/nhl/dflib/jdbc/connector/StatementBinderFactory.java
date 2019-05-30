package com.nhl.dflib.jdbc.connector;

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @since 0.6
 */
public class StatementBinderFactory {

    private StatementPositionBinderFactory defaultPositionBinderFactory;
    private Map<Integer, StatementPositionBinderFactory> positionBinderFactories;

    public StatementBinderFactory() {
        this.defaultPositionBinderFactory = StatementPositionBinderFactory::objectBinder;
        this.positionBinderFactories = new HashMap<>();
        this.positionBinderFactories.put(Types.DATE, StatementPositionBinderFactory::dateBinder);
        this.positionBinderFactories.put(Types.TIME, StatementPositionBinderFactory::timeBinder);
        this.positionBinderFactories.put(Types.TIMESTAMP, StatementPositionBinderFactory::timestampBinder);
        this.positionBinderFactories.put(Types.INTEGER, StatementPositionBinderFactory::intBinder);
        this.positionBinderFactories.put(Types.VARCHAR, StatementPositionBinderFactory::stringBinder);
    }

    public StatementBinder createBinder(PreparedStatement statement) throws SQLException {

        ParameterMetaData pmd = statement.getParameterMetaData();
        int len = pmd.getParameterCount();
        StatementPositionBinder[] binders = new StatementPositionBinder[len];

        for (int i = 0; i < len; i++) {
            int jdbcPos = i + 1;
            int jdbcType = pmd.getParameterType(jdbcPos);
            binders[i] = positionBinder(statement, jdbcType, jdbcPos);
        }

        return new StatementBinder(binders);
    }

    private StatementPositionBinder positionBinder(PreparedStatement statement, int type, int pos) {
        return positionBinderFactories
                .getOrDefault(type, defaultPositionBinderFactory)
                .binder(new StatementPosition(statement, type, pos));
    }
}
