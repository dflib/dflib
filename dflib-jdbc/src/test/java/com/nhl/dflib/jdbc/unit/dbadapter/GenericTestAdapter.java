package com.nhl.dflib.jdbc.unit.dbadapter;

import io.bootique.jdbc.junit5.DbTester;

public class GenericTestAdapter implements TestDbAdapter {

    private DbTester db;

    public GenericTestAdapter(DbTester db) {
        this.db = db;
    }

    @Override
    public DbTester getDb() {
        return db;
    }

    @Override
    public String toNativeSql(String derbySql, String... params) {
        return String.format(derbySql, params);
    }
}
