package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.jdbc.connector.metadata.DbColumnMetadata;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @since 0.6
 */
public interface StatementBinderFactory {

    StatementBinder createBinder(PreparedStatement statement) throws SQLException;

    StatementBinderFactory withFixedParams(DbColumnMetadata[] params);
}
