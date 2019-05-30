package com.nhl.dflib.jdbc.connector.statement;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.jdbc.connector.JdbcFunction;
import com.nhl.dflib.jdbc.connector.metadata.DbColumnMetadata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @since 0.6
 */
public class StatementBuilder {

    private JdbcConnector connector;

    public StatementBuilder(JdbcConnector connector) {
        this.connector = connector;
    }

    private String sql;
    private DbColumnMetadata[] paramDescriptors;
    private Object[] params;
    private DataFrame batchParams;

    public StatementBuilder sql(String sql) {
        this.sql = sql;
        return this;
    }

    public StatementBuilder paramDescriptors(DbColumnMetadata[] paramDescriptors) {
        this.paramDescriptors = paramDescriptors;
        return this;
    }

    public StatementBuilder bindBatch(DataFrame batchParams) {
        this.params = null;
        this.batchParams = batchParams;
        return this;
    }

    public StatementBuilder bind(Object[] params) {
        this.params = params;
        this.batchParams = null;
        return this;
    }

    public <T> T select(Connection connection, JdbcFunction<ResultSet, T> resultReader) {
        try {
            return createSelectStatement().select(connection, resultReader);
        } catch (SQLException e) {
            throw new RuntimeException("Error loading data from DB", e);
        }
    }

    public void update(Connection connection) {
        try {
            createUpdateStatement().update(connection);
        } catch (SQLException e) {
            throw new RuntimeException("Error updating data in DB", e);
        }
    }

    protected SelectStatement createSelectStatement() {
        if (batchParams != null) {
            throw new IllegalStateException("Can't use batch params for 'select'");
        }

        return (params == null || params.length == 0)
                ? new SelectStatementNoParams(sql)
                : new SelectStatementWithParams(sql, params, createBinderFactory());
    }

    protected UpdateStatement createUpdateStatement() {
        if (params != null) {
            return new UpdateStatementParamsRow(sql, params, connector.getBinderFactory());
        } else if (batchParams != null) {

            return connector.getMetadata().supportsBatchUpdates()
                    ? new UpdateStatementBatch(sql, batchParams, createBinderFactory())
                    : new UpdateStatementNoBatch(sql, batchParams, createBinderFactory());

        } else {
            return new UpdateStatementNoParams(sql);
        }
    }

    protected StatementBinderFactory createBinderFactory() {
        return paramDescriptors != null
                ? connector.getBinderFactory()
                : connector.getBinderFactory().withFixedParams(paramDescriptors);
    }
}
