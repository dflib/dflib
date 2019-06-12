package com.nhl.dflib.jdbc.connector.tx;

import com.nhl.dflib.jdbc.connector.JdbcConnector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A builder of a single DB transaction that allows to configure transaction parameters and run multiple operations
 * within the transaction.
 *
 * @since 0.6
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

    public void run(Consumer<JdbcConnector> op) {
        call(c -> {
            op.accept(c);
            return null;
        });
    }

    public <T> T call(Function<JdbcConnector, T> op) {

        try (Connection connection = connector.getConnection()) {

            if (isolation != null) {
                connection.setTransactionIsolation(isolation.value);
            }

            TxConnectionWrapper connectionWrapper = new TxConnectionWrapper(connection);

            try {
                T result = op.apply(new TxJdbcConnector(connector, connectionWrapper));
                connection.commit();
                return result;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
