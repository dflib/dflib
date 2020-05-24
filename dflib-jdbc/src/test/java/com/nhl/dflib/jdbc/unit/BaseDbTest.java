package com.nhl.dflib.jdbc.unit;

import com.nhl.dflib.jdbc.unit.dbadapter.GenericTestAdapter;
import com.nhl.dflib.jdbc.unit.dbadapter.MySQLTestAdapter;
import com.nhl.dflib.jdbc.unit.dbadapter.TestDbAdapter;
import io.bootique.jdbc.junit5.DbTester;
import io.bootique.junit5.BQTest;
import org.junit.jupiter.api.extension.RegisterExtension;

@BQTest
public abstract class BaseDbTest {

    protected static final String TEST_DB_PROPERTY = "test.db";

    private static DbTester createDbTester(String dbType) {
        switch (dbType) {
            case "mysql":
                return DbTester
                        .testcontainersDb("jdbc:tc:mysql:5.7://localhost/test?generateSimpleParameterMetadata=true")
                        .initDB("classpath:com/nhl/dflib/jdbc/init_schema_mysql.sql", "--");
            case "postgresql":
                return DbTester
                        .testcontainersDb("jdbc:tc:postgresql:11://localhost/test")
                        .initDB("classpath:com/nhl/dflib/jdbc/init_schema_postgresql.sql", "--");
            case "derby":
                return DbTester
                        .derbyDb()
                        .initDB("classpath:com/nhl/dflib/jdbc/init_schema_derby.sql", "--");
            default:
                throw new IllegalArgumentException("Unsupported DB type: " + dbType);
        }
    }

    private static TestDbAdapter createDbAdapter(String dbType, DbTester db) {
        switch (dbType) {
            case "mysql":
                return new MySQLTestAdapter(db);
            default:
                return new GenericTestAdapter(db);
        }
    }

    @RegisterExtension
    protected static final DbTester db =
            createDbTester(System.getProperty(TEST_DB_PROPERTY, "derby"))
                    .deleteBeforeEachTest("t1", "t2", "t3", "t1_audit");

    protected static TestDbAdapter adapter = createDbAdapter(System.getProperty(TEST_DB_PROPERTY, "derby"), db);
}
