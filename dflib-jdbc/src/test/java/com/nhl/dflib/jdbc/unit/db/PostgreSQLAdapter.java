package com.nhl.dflib.jdbc.unit.db;

class PostgreSQLAdapter implements TestDbAdapter {
    @Override
    public String toNativeSql(String command) {
        return command;
    }
}
