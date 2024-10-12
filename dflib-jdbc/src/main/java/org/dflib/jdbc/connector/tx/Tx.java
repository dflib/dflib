package org.dflib.jdbc.connector.tx;

import org.dflib.jdbc.connector.JdbcConnector;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Provides API to build a DB transaction wrapper around a set of JDBC operations.
 */
public class Tx {

    private JdbcConnector connector;
    private TxIsolation isolation;

    protected Tx(JdbcConnector connector) {
        this.connector = Objects.requireNonNull(connector);
    }

    public static Tx newTransaction(JdbcConnector connector) {
        return new Tx(connector);
    }

    public Tx isolation(TxIsolation isolation) {
        this.isolation = Objects.requireNonNull(isolation);
        return this;
    }

    /**
     * Executes operation that returns no result. The operation is wrapped in transaction. So all database operations
     * will be committed or rolled back together.
     *
     * @param op an operation to run in transaction
     */
    public void run(Consumer<JdbcConnector> op) {
        call(c -> {
            op.accept(c);
            return null;
        });
    }

    /**
     * Executes operation that returns a result. The operation is wrapped in transaction. So all database operations
     * will be committed or rolled back together.
     *
     * @param op an operation to run in transaction
     * @return the result of "op" if transaction is successfully committed. Throws an exception otherwise.
     */
    public <T> T call(Function<JdbcConnector, T> op) {

        try (TxJdbcConnector txConnector = new TxJdbcConnector(connector, isolation)) {
            try {
                T result = op.apply(txConnector);
                txConnector.commit();
                return result;
            }
            // catching Exception, not SQLException, to ensure we rollback even if the error is not originating in the DB
            catch (Exception e) {
                txConnector.rollback();
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }
}
