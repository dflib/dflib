package com.nhl.dflib.jdbc.connector.statement;

import com.nhl.dflib.jdbc.connector.metadata.DbColumnMetadata;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @since 0.6
 */
public interface StatementBinderFactory {

    StatementBinder createBinder(PreparedStatement statement) throws SQLException;

    /**
     * Creates a derived binder factory for a known set of binding parameters described by {@link DbColumnMetadata}
     * array argument. This only works for a known PreparedStatement structure and avoids the need to read
     * parameters metadata from a PreparedStatement (as some DBs won't provide them correctly).
     *
     * @param paramsMetadata an array of parameter descriptors that must match the number and type of bindings in
     *                       any PreparedStatement passed to {@link #createBinder(PreparedStatement)}.
     * @return a new amended StatementBinderFactory
     */
    // TODO: dirty
    StatementBinderFactory withFixedParams(DbColumnMetadata[] paramsMetadata);
}
