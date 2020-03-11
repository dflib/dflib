package com.nhl.dflib.jdbc.unit.db;

import com.nhl.dflib.jdbc.unit.DbInitializer;

class PostgreSQLAdapter implements DBAdapter {

    /* Use "#" delimiter because in postgreSQL initialize script we use functions in trigger.
    * Our DbInitializer class use ";" as Scanner delimiter. Postgres function must be indivisible,
    * but scanner cut them into some parts which leading to an error */
    private static final String DELIMITER = "#";
    private static final String SQL_SCHEMA_PATH = "classpath:com/nhl/dflib/jdbc/init_schema_postgresql.sql";
    private static final String DB_TYPE = "postgresql";

    @Override
    public String getDBType() {
        return DB_TYPE;
    }

    @Override
    public String processSQL(String command) {
        return command;
    }

    @Override
    public DbInitializer getInitializer() {
        return new DbInitializer(SQL_SCHEMA_PATH, DELIMITER);
    }
}
