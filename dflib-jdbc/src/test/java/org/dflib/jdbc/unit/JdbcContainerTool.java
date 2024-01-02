package org.dflib.jdbc.unit;

import io.bootique.jdbc.junit5.DbTester;
import io.bootique.jdbc.junit5.derby.DerbyTester;
import io.bootique.jdbc.junit5.tc.TcDbTester;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MySQLContainer;

public class JdbcContainerTool {

    private static final String MYSQL_PASSWORD = "test";

    private String dbType;
    private JdbcDatabaseContainer maybeContainer;

    public static JdbcContainerTool create(String dbType) {
        switch (dbType) {

            // MySQL is special ... We need a "root" user to setup schema, and that requires an explicit container object
            case "mysql":
                JdbcDatabaseContainer container = new MySQLContainer("mysql:8.0.20").withPassword(MYSQL_PASSWORD);

                // we are working in a GLOBAL scope, so just start the DB as early as we can, and don't worry about
                // shutdown...
                container.start();

                return new JdbcContainerTool(dbType, container);
            default:
                return new JdbcContainerTool(dbType, null);
        }
    }

    protected JdbcContainerTool(String dbType, JdbcDatabaseContainer maybeContainer) {
        this.dbType = dbType;
        this.maybeContainer = maybeContainer;
    }

    public DbTester createDbTester() {
        switch (dbType) {
            case "mysql":
                return TcDbTester
                        // password is the same for "root", as the one for non-root user above
                        .db(maybeContainer, "root", MYSQL_PASSWORD)
                        .initDB("classpath:org/dflib/jdbc/init_schema_mysql.sql", "--");
            case "postgresql":
                return TcDbTester
                        .db("jdbc:tc:postgresql:11://localhost/test")
                        .initDB("classpath:org/dflib/jdbc/init_schema_postgresql.sql", "--");
            case "derby":
                return DerbyTester
                        .db()
                        .initDB("classpath:org/dflib/jdbc/init_schema_derby.sql", "--");
            default:
                throw new IllegalArgumentException("Unsupported DB type: " + dbType);
        }
    }
}
