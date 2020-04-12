package com.nhl.dflib.jdbc.unit;

import com.nhl.dflib.jdbc.unit.dbadapter.TestDbAdapter;
import io.bootique.jdbc.test.Column;
import io.bootique.jdbc.test.Table;
import io.bootique.jdbc.test.TestDataManager;
import io.bootique.test.junit.BQTestFactory;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;

import javax.sql.DataSource;
import java.util.List;

import static java.util.stream.Collectors.toList;

public abstract class BaseDbTest {

    @ClassRule
    public static BQTestFactory TEST_FACTORY = new BQTestFactory();

    protected static Table T1;
    protected static Table T2;
    protected static Table T3;
    protected static Table T1_AUDIT;

    private static TestDbAdapter DB_ADAPTER;
    private static DataSource DATA_SOURCE;

    @Rule
    public final TestDataManager dataManager = new TestDataManager(true, T1, T2, T3, T1_AUDIT);

    @BeforeClass
    public static void initDB() {
        String dbType = System.getProperty("test.db", "derby");
        DbBootstrap bootstrap = DbBootstrap.create(TEST_FACTORY, dbType);

        DATA_SOURCE = bootstrap.getDataSource();
        T1 = bootstrap.getT1();
        T2 = bootstrap.getT2();
        T3 = bootstrap.getT3();
        T1_AUDIT = bootstrap.getT1Audit();

        DB_ADAPTER = bootstrap.getDbAdapter();
    }

    protected static List<String> columnNames(Table table) {
        return table.getColumns().stream().map(Column::getName).collect(toList());
    }

    protected DataSource getDataSource() {
        return DATA_SOURCE;
    }

    protected String toNativeSql(String derbySql) {
        return DB_ADAPTER.toNativeSql(derbySql);
    }
}
