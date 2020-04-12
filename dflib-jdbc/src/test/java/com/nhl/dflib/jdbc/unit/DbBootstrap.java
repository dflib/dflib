package com.nhl.dflib.jdbc.unit;

import com.nhl.dflib.jdbc.unit.db.DBAdapter;
import io.bootique.BQRuntime;
import io.bootique.jdbc.DataSourceFactory;
import io.bootique.jdbc.JdbcModule;
import io.bootique.jdbc.test.Table;
import io.bootique.jdbc.test.runtime.DatabaseChannelFactory;
import io.bootique.test.junit.BQTestFactory;

import javax.sql.DataSource;

public class DbBootstrap {

    private BQRuntime runtime;

    private String dataSourceName;

    public DbBootstrap(BQRuntime runtime, String dataSourceName) {
        this.runtime = runtime;
        this.dataSourceName = dataSourceName.toLowerCase();
    }

    public static DbBootstrap create(BQTestFactory testFactory, DBAdapter dbAdapter) {
        BQRuntime runtime = testFactory.app("-c", "classpath:com/nhl/dflib/jdbc/jdbc.yml")
                .autoLoadModules()
                .module(b -> JdbcModule.extend(b).addDataSourceListener(dbAdapter.getInitializer()))
                .createRuntime();
        return new DbBootstrap(runtime, dbAdapter.getDBType());
    }

    public DataSource getDataSource() {
        return runtime.getInstance(DataSourceFactory.class).forName(dataSourceName);
    }

    public Table getT1() {
        return runtime.getInstance(DatabaseChannelFactory.class)
                .getChannel(dataSourceName)
                .newTable("t1")
                .columnNames("id", "name", "salary")
                .initColumnTypesFromDBMetadata()
                .build();
    }

    public Table getT1Audit() {
        return runtime.getInstance(DatabaseChannelFactory.class)
                .getChannel(dataSourceName)
                .newTable("t1_audit")
                .columnNames("id", "op", "op_id")
                .initColumnTypesFromDBMetadata()
                .build();
    }

    public Table getT2() {
        return runtime.getInstance(DatabaseChannelFactory.class)
                .getChannel(dataSourceName)
                .newTable("t2")
                .columnNames("bigint", "int", "double", "boolean", "string", "timestamp", "date", "time", "bytes")
                .initColumnTypesFromDBMetadata()
                .build();
    }

    public Table getT3() {
        return runtime.getInstance(DatabaseChannelFactory.class)
                .getChannel(dataSourceName)
                .newTable("t3")
                .columnNames("int", "long", "double", "boolean")
                .initColumnTypesFromDBMetadata()
                .build();
    }
}
