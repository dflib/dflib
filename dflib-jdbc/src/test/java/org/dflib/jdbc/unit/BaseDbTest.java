package org.dflib.jdbc.unit;

import org.dflib.jdbc.unit.dbadapter.GenericTestAdapter;
import org.dflib.jdbc.unit.dbadapter.MySQLTestAdapter;
import org.dflib.jdbc.unit.dbadapter.TestDbAdapter;
import io.bootique.jdbc.junit5.DbTester;
import io.bootique.junit5.BQTest;
import io.bootique.junit5.BQTestScope;
import io.bootique.junit5.BQTestTool;

@BQTest
public abstract class BaseDbTest {

    protected static final String TEST_DB_PROPERTY = "test.db";

    private static String dbType() {
        return System.getProperty(TEST_DB_PROPERTY, "derby");
    }

    private static TestDbAdapter createDbAdapter(String dbType, DbTester db) {
        switch (dbType) {
            case "mysql":
                return new MySQLTestAdapter(db);
            default:
                return new GenericTestAdapter(db);
        }
    }

    @BQTestTool(BQTestScope.GLOBAL)
    protected static final DbTester db = JdbcContainerTool
            .create(dbType())
            .createDbTester()
            .deleteBeforeEachTest("t1", "t2", "t3", "t1_audit");

    protected static final TestDbAdapter adapter = createDbAdapter(dbType(), db);
}
