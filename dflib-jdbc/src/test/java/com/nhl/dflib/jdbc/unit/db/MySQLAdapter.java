package com.nhl.dflib.jdbc.unit.db;

import com.nhl.dflib.jdbc.unit.DbInitializer;

class MySQLAdapter implements TestDbAdapter {

    private static final String QUOTE = "`";
    private static final String DELIMITER = ";";

    @Override
    public String toNativeSql(String command) {
        if (command.contains("SUBSTR")) {
            return command
                    .replaceAll("\"", QUOTE)
                    .replaceAll("SUBSTR", "SUBSTRING");
        }
        return command.replaceAll("\"", QUOTE);
    }

    @Override
    public DbInitializer getInitializer(String initSchemaFile) {
        return new DbInitializer(initSchemaFile, DELIMITER);
    }
}
