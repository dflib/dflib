package com.nhl.dflib.jdbc.unit.db;

import com.nhl.dflib.jdbc.unit.DbInitializer;

class DerbyAdapter implements DBAdapter {

    private static final String DELIMITER = ";";
    private static final String SQL_SCHEMA_PATH = "classpath:com/nhl/dflib/jdbc/init_schema.sql";
    private static final String DB_TYPE = "derby";

    @Override
    public String processSQL(String command) {
        return command;
    }

    @Override
    public DbInitializer getInitializer() {
        return new DbInitializer(SQL_SCHEMA_PATH, DELIMITER);
    }

    @Override
    public String getDBType() {
        return DB_TYPE;
    }
}
