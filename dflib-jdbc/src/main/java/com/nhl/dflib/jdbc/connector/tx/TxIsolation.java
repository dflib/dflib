package com.nhl.dflib.jdbc.connector.tx;

import java.sql.Connection;

/**
 * Defines transaction isolation levels.
 *
 * @since 0.6
 */
public enum TxIsolation {

    read_uncommitted(Connection.TRANSACTION_READ_UNCOMMITTED),

    read_committed(Connection.TRANSACTION_READ_COMMITTED),

    repeatable_read(Connection.TRANSACTION_REPEATABLE_READ),

    serializable(Connection.TRANSACTION_SERIALIZABLE),

    none(Connection.TRANSACTION_NONE);

    /**
     * Stores an int value for transaction isolation, compatible with the values defined in the {@link Connection} class.
     */
    public final int value;

    TxIsolation(int value) {
        this.value = value;
    }
}
