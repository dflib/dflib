package com.nhl.dflib.jdbc.unit;

import io.bootique.jdbc.test.Table;
import io.bootique.test.junit.BQTestFactory;
import org.junit.BeforeClass;
import org.junit.ClassRule;

import javax.sql.DataSource;

public abstract class BaseDbTest {

    @ClassRule
    public static BQTestFactory TEST_FACTORY = new BQTestFactory();

    protected static Table T1;
    private static DataSource DATA_SOURCE;

    @BeforeClass
    public static void initDataSource() {
        DbBootstrap bootstrap = DbBootstrap.create(TEST_FACTORY, "classpath:com/nhl/dflib/jdbc/init_schema.sql");
        DATA_SOURCE = bootstrap.getDataSource();
        T1 = bootstrap.getT1();
    }

    protected DataSource getDataSource() {
        return DATA_SOURCE;
    }
}
