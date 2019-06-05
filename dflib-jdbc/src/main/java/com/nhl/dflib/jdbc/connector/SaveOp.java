package com.nhl.dflib.jdbc.connector;

/**
 * An enum that specifies possible outcomes for DataFrame rows during save via {@link TableLoader}.
 *
 * @since 0.6
 */
public enum SaveOp {

    insert, update, skip
}
