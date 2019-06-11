package com.nhl.dflib.jdbc.connector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Consumer;

/**
 * @since 0.6
 */
public class TxManager {

    private JdbcConnector connector;

    public TxManager(JdbcConnector connector) {
        this.connector = connector;
    }

    public void runInTx(Consumer<JdbcConnector> op) {

        try (Connection connection = connector.getConnection()) {
            TxConnectionWrapper connectionWrapper = new TxConnectionWrapper(connection);

            try {
                op.accept(new TxJdbcConnector(connector, connectionWrapper));
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
