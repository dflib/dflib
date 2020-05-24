package com.nhl.dflib.jdbc.connector.tx;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.jdbc.connector.JdbcConnector;
import com.nhl.dflib.jdbc.unit.BaseDbTest;
import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperties;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TxIT extends BaseDbTest {

    @Test
    public void testRun() {

        adapter.delete("t1");

        JdbcConnector connector = adapter.createConnector();
        DataFrame df1 = DataFrame.newFrame("id", "name", "salary")
                .foldByRow(
                        1L, "n1", 50_000.01,
                        2L, "n2", 120_000.);

        DataFrame df2 = DataFrame.newFrame("id", "name", "salary")
                .foldByRow(
                        3L, "n3", 60_000.01,
                        4L, "n4", 1_000.);

        Tx.newTransaction(connector)
                .run(c -> {
                            c.tableSaver("t1").save(df1);
                            c.tableSaver("t1").save(df2);
                        }
                );

        DataFrame df_12 = connector
                .tableLoader("t1")
                .load()
                .sort("id", true);

        new DataFrameAsserts(df_12, "id", "name", "salary")
                .expectHeight(4)
                .expectRow(0, 1L, "n1", 50_000.01)
                .expectRow(1, 2L, "n2", 120_000.)
                .expectRow(2, 3L, "n3", 60_000.01)
                .expectRow(3, 4L, "n4", 1_000.);
    }

    @Test
    public void testRun_Isolation() {

        JdbcConnector connector = adapter.createConnector();
        Tx.newTransaction(connector)
                .isolation(TxIsolation.read_committed).run(txConnector -> {
                    int il;
                    try {
                        il = txConnector.getConnection().getTransactionIsolation();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    assertEquals(Connection.TRANSACTION_READ_COMMITTED, il);
                }
        );

        Tx.newTransaction(connector)
                .isolation(TxIsolation.serializable).run(txConnector -> {
                    int il;
                    try {
                        il = txConnector.getConnection().getTransactionIsolation();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    assertEquals(Connection.TRANSACTION_SERIALIZABLE, il);
                }
        );
    }

    @Test
    // TODO: issues with Derby rollback leaving a lock around
    @EnabledIfSystemProperties({
            @EnabledIfSystemProperty(named = TEST_DB_PROPERTY, matches = "mysql"),
            @EnabledIfSystemProperty(named = TEST_DB_PROPERTY, matches = "postgresql")})
    public void testRun_Rollback() {
        adapter.delete("t1");
        JdbcConnector connector = adapter.createConnector();
        DataFrame df1 = DataFrame.newFrame("id", "name", "salary")
                .foldByRow(
                        1L, "n1", 50_000.01,
                        2L, "n2", 120_000.);

        DataFrame df2 = DataFrame.newFrame("id", "name", "salary")
                .foldByRow(
                        3L, "n3", 60_000.01,
                        4L, "n4", 1_000.);

        assertThrows(RuntimeException.class, () ->
                Tx.newTransaction(connector).run(c -> {
                            c.tableSaver("t1").save(df1);
                            c.tableSaver("no_such_table").save(df2);
                        }
                )
        );

        DataFrame df_12 = connector.tableLoader("t1").load();

        // the transaction must have been rolled back and no data saved
        new DataFrameAsserts(df_12, "id", "name", "salary").expectHeight(0);
    }
}
