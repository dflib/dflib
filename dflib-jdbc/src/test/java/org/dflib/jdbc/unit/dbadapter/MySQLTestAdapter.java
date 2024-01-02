package org.dflib.jdbc.unit.dbadapter;

import io.bootique.jdbc.junit5.DbTester;

public class MySQLTestAdapter implements TestDbAdapter {

    private static final String QUOTE = "`";

    private DbTester db;

    public MySQLTestAdapter(DbTester db) {
        this.db = db;
    }

    @Override
    public DbTester getDb() {
        return db;
    }

    @Override
    public String toNativeSql(String derbySql, String... params) {

        String withParams = String.format(derbySql, params);
        if (withParams.contains("SUBSTR")) {
            return withParams
                    .replaceAll("\"", QUOTE)
                    .replaceAll("SUBSTR", "SUBSTRING");
        }
        return withParams.replaceAll("\"", QUOTE);
    }
}
