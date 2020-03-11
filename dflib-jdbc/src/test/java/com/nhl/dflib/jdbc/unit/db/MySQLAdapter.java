package com.nhl.dflib.jdbc.unit.db;

import com.nhl.dflib.jdbc.unit.DbInitializer;

class MySQLAdapter implements DBAdapter {

    private static final String QUOTE = "`";
    private static final String DELIMITER = ";";
    private static final String SQL_SCHEMA_PATH = "classpath:com/nhl/dflib/jdbc/init_schema_mysql.sql";
    private static final String DB_TYPE = "mysql";

    @Override
    public String processSQL(String command) {
        if (command.contains("SUBSTR")) {
            return command
                    .replaceAll("\"", QUOTE)
                    .replaceAll("SUBSTR", "SUBSTRING");
        }
        return command.replaceAll("\"", QUOTE);
    }

    @Override
    public DbInitializer getInitializer() {
        System.setProperty("user.timezone", "UTC");
        return new DbInitializer(SQL_SCHEMA_PATH, DELIMITER);
    }

    @Override
    public String getDBType() {
        return DB_TYPE;
    }
}
