package com.nhl.dflib.jdbc.unit;

import com.nhl.dflib.jdbc.unit.dbadapter.TestDbAdapter;
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
    private TestDbAdapter dbAdapter;

    public DbBootstrap(BQRuntime runtime, TestDbAdapter dbAdapter, String dataSourceName) {
        this.runtime = runtime;
        this.dataSourceName = dataSourceName;
        this.dbAdapter = dbAdapter;
    }

    public static DbBootstrap create(BQTestFactory testFactory, String dbType) {

        String configFile = "classpath:com/nhl/dflib/jdbc/" + dbType + ".yml";
        String initSchemaFile = "classpath:com/nhl/dflib/jdbc/init_schema_" + dbType + ".sql";
        
        BQRuntime runtime = testFactory.app("-c", configFile)
                .autoLoadModules()
                .module(b -> JdbcModule.extend(b).addDataSourceListener(new DbInitializer(initSchemaFile)))
                .createRuntime();
        return new DbBootstrap(runtime, TestDbAdapter.createAdapter(dbType), dbType);
    }

    public TestDbAdapter getDbAdapter() {
        return dbAdapter;
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
