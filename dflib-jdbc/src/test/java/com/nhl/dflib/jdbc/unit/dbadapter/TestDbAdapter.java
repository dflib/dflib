package com.nhl.dflib.jdbc.unit.dbadapter;

public interface TestDbAdapter {

    static TestDbAdapter createAdapter(String dbType) {
        switch (dbType) {
            case "mysql":
                return new MySQLTestAdapter();
            case "postgresql":
            case "derby":
                return new GenericTestAdapter();
            default:
                throw new IllegalArgumentException("Unsupported DB type: " + dbType);
        }
    }

    String toNativeSql(String derbySql);
}
