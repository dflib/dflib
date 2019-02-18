package com.nhl.dflib.jdbc;

import io.bootique.BQCoreModule;
import io.bootique.BQRuntime;
import io.bootique.jdbc.DataSourceFactory;
import io.bootique.test.junit.BQTestFactory;

import javax.sql.DataSource;

public class DbBootstrap {

    public static DataSource dataSource(BQTestFactory testFactory, String initFile) {
        BQRuntime runtime = testFactory.app()
                .autoLoadModules()
                .module(b -> BQCoreModule.extend(b).setProperty("bq.jdbc.ds.jdbcUrl", "jdbc:derby:target/derby/testdb0;create=true"))
//                .module(b -> JdbcModule.extend(b).addDataSourceListener())
                .createRuntime();

        return runtime.getInstance(DataSourceFactory.class).forName("ds");
    }
}
