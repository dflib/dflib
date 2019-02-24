package com.nhl.dflib.jdbc.unit;

import io.bootique.jdbc.test.Table;
import io.bootique.jdbc.test.TestDataManager;
import io.bootique.test.junit.BQTestFactory;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;

import javax.sql.DataSource;

public abstract class BaseDbTest {

    @ClassRule
    public static BQTestFactory TEST_FACTORY = new BQTestFactory();

    protected static Table T1;
    protected static Table T2;
    private static DataSource DATA_SOURCE;

    @Rule
    public final TestDataManager dataManager = new TestDataManager(true, T1, T2);

    @BeforeClass
    public static void initDataSource() {
        DbBootstrap bootstrap = DbBootstrap.create(TEST_FACTORY, "classpath:com/nhl/dflib/jdbc/init_schema.sql");
        DATA_SOURCE = bootstrap.getDataSource();
        T1 = bootstrap.getT1();
        T2 = bootstrap.getT2();
    }

    protected DataSource getDataSource() {
        return DATA_SOURCE;
    }
}