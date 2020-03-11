package com.nhl.dflib.jdbc.unit.db;

import com.nhl.dflib.jdbc.unit.DbInitializer;

public interface DBAdapter {

    static DBAdapter createAdapter() {
        String dbType = System.getProperty("db", "derby");
        switch (dbType.toLowerCase()) {
            case "postgresql":
                return new PostgreSQLAdapter();
            case "mysql":
                return new MySQLAdapter();
            default: return new DerbyAdapter();
        }
    }

    String processSQL(String sql);

    String getDBType();

    DbInitializer getInitializer();
}
