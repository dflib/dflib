package com.nhl.dflib.jdbc.unit;

import com.nhl.dflib.jdbc.unit.dbadapter.GenericTestAdapter;
import com.nhl.dflib.jdbc.unit.dbadapter.MySQLTestAdapter;
import com.nhl.dflib.jdbc.unit.dbadapter.TestDbAdapter;
import io.bootique.jdbc.junit5.DbTester;
import io.bootique.junit5.BQTest;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.stream.Stream;

@BQTest
public abstract class BaseDbTest {

    protected static final String DB_ADAPTERS_METHOD = "dbAdapters";

    // Don't delete data in DbTester, as different testers are invoked for different test parameters.
    // Tests will manage deletion by calling "deleteTestData"

    @RegisterExtension
    protected static final DbTester derbyDb = DbTester
            .derbyDb()
            .initDB("classpath:com/nhl/dflib/jdbc/init_schema_derby.sql", "--");

    @RegisterExtension
    protected static final DbTester postgresDb = DbTester
            .testcontainersDb("jdbc:tc:postgresql:11://localhost/test")
            .initDB("classpath:com/nhl/dflib/jdbc/init_schema_postgresql.sql", "--");

    @RegisterExtension
    protected static final DbTester mysqlDb = DbTester
            .testcontainersDb("jdbc:tc:mysql:5.7://localhost/test?generateSimpleParameterMetadata=true")
            .initDB("classpath:com/nhl/dflib/jdbc/init_schema_mysql.sql", "--");

    // used in parameterized tests
    protected static TestDbAdapter derbyAdapter = new GenericTestAdapter(derbyDb);
    protected static TestDbAdapter postgresAdapter = new GenericTestAdapter(postgresDb);
    protected static TestDbAdapter mysqlAdapter = new MySQLTestAdapter(mysqlDb);

    protected static Stream<TestDbAdapter> dbAdapters() {
        return Stream.of(derbyAdapter, postgresAdapter, mysqlAdapter);
    }

    protected void deleteTestData(TestDbAdapter adapter) {
        adapter.delete("t1", "t2", "t3", "t1_audit");
    }
}
