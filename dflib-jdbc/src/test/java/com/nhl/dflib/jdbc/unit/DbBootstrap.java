package com.nhl.dflib.jdbc.unit;

import io.bootique.BQCoreModule;
import io.bootique.BQRuntime;
import io.bootique.jdbc.DataSourceFactory;
import io.bootique.jdbc.JdbcModule;
import io.bootique.jdbc.test.Table;
import io.bootique.jdbc.test.runtime.DatabaseChannelFactory;
import io.bootique.test.junit.BQTestFactory;

import javax.sql.DataSource;

public class DbBootstrap {

    private BQRuntime runtime;

    public DbBootstrap(BQRuntime runtime) {
        this.runtime = runtime;
    }

    public static DbBootstrap create(BQTestFactory testFactory, String initFile) {
        BQRuntime runtime = testFactory.app()
                .autoLoadModules()
                .module(b -> BQCoreModule.extend(b).setProperty("bq.jdbc.ds.jdbcUrl", "jdbc:derby:target/derby/testdb0;create=true"))
                .module(b -> JdbcModule.extend(b).addDataSourceListener(new DbInitializer(initFile)))
                .createRuntime();

        return new DbBootstrap(runtime);
    }

    public DataSource getDataSource() {
        return runtime.getInstance(DataSourceFactory.class).forName("ds");
    }

    public Table getT1() {
        return runtime.getInstance(DatabaseChannelFactory.class)
                .getChannel()
                .newTable("t1")
                .columnNames("id", "name", "salary")
                .initColumnTypesFromDBMetadata()
                .build();
    }

    public Table getT1Audit() {
        return runtime.getInstance(DatabaseChannelFactory.class)
                .getChannel()
                .newTable("t1_audit")
                .columnNames("id", "op", "op_id")
                .initColumnTypesFromDBMetadata()
                .build();
    }

    public Table getT2() {
        return runtime.getInstance(DatabaseChannelFactory.class)
                .getChannel()
                .newTable("t2")
                .columnNames("bigint", "int", "double", "boolean", "string", "timestamp", "date", "time", "bytes")
                .initColumnTypesFromDBMetadata()
                .build();
    }

    public Table getT3() {
        return runtime.getInstance(DatabaseChannelFactory.class)
                .getChannel()
                .newTable("t3")
                .columnNames("int", "long", "double", "boolean")
                .initColumnTypesFromDBMetadata()
                .build();
    }
}
