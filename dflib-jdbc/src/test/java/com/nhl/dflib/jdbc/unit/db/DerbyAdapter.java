package com.nhl.dflib.jdbc.unit.db;

class DerbyAdapter implements TestDbAdapter {

    @Override
    public String toNativeSql(String command) {
        return command;
    }
}
