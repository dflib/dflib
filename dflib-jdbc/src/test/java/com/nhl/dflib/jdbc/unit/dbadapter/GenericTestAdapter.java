package com.nhl.dflib.jdbc.unit.dbadapter;

class GenericTestAdapter implements TestDbAdapter {

    @Override
    public String toNativeSql(String command) {
        return command;
    }
}
