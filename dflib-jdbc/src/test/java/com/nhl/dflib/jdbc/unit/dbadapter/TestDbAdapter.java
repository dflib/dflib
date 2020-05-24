package com.nhl.dflib.jdbc.unit.dbadapter;

import com.nhl.dflib.jdbc.Jdbc;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import io.bootique.jdbc.junit5.DbTester;
import io.bootique.jdbc.junit5.Table;
import io.bootique.jdbc.junit5.metadata.DbColumnMetadata;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public interface TestDbAdapter {

    DbTester getDb();

    String toNativeSql(String derbySql);

    default JdbcConnector createConnector() {
        return Jdbc.connector(getDb().getDataSource());
    }

    default Table getTable(String name) {
        return getDb().getTable(name);
    }

    default List<String> getColumnNames(String tableName) {
        Table table = getTable(tableName);
        return Stream.of(table.getMetadata().getColumns()).map(DbColumnMetadata::getName).collect(toList());
    }

    default void delete(String... tablesNamesInInsertOrder) {
        for (int i = tablesNamesInInsertOrder.length - 1; i >= 0; i--) {
            getTable(tablesNamesInInsertOrder[i]).deleteAll();
        }
    }
}
