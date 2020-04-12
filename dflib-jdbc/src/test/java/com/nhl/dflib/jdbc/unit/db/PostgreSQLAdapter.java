package com.nhl.dflib.jdbc.unit.db;

import com.nhl.dflib.jdbc.unit.DbInitializer;

class PostgreSQLAdapter implements TestDbAdapter {

    // TODO: suspect
    /* Use "#" delimiter because in postgreSQL initialize script we use functions in trigger.
    * Our DbInitializer class use ";" as Scanner delimiter. Postgres function must be indivisible,
    * but scanner cut them into some parts which leading to an error */
    private static final String DELIMITER = "#";

    @Override
    public String toNativeSql(String command) {
        return command;
    }

    @Override
    public DbInitializer getInitializer(String initSchemaFile) {
        return new DbInitializer(initSchemaFile, DELIMITER);
    }
}
