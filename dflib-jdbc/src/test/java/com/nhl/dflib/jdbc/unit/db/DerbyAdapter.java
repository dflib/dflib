package com.nhl.dflib.jdbc.unit.db;

import com.nhl.dflib.jdbc.unit.DbInitializer;

class DerbyAdapter implements TestDbAdapter {

    private static final String DELIMITER = ";";

    @Override
    public String toNativeSql(String command) {
        return command;
    }

    @Override
    public DbInitializer getInitializer(String initSchemaFile) {
        return new DbInitializer(initSchemaFile, DELIMITER);
    }

}
