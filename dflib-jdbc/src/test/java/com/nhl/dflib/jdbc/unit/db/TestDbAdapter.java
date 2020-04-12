package com.nhl.dflib.jdbc.unit.db;

import com.nhl.dflib.jdbc.unit.DbInitializer;

public interface TestDbAdapter {

    static TestDbAdapter createAdapter(String dbType) {
        switch (dbType) {
            case "postgresql":
                return new PostgreSQLAdapter();
            case "mysql":
                return new MySQLAdapter();
            case "derby":
                return new DerbyAdapter();
            default:
                throw new IllegalArgumentException("Unsupported DB type: " + dbType);
        }
    }

    String toNativeSql(String derbySql);

    DbInitializer getInitializer(String initSchemaFile);
}
