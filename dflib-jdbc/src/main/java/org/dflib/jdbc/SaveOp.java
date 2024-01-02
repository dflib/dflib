package org.dflib.jdbc;

import org.dflib.DataFrame;
import org.dflib.jdbc.connector.TableSaver;

/**
 * Defines possible outcomes for DataFrame rows during when saving via {@link TableSaver}.
 * A <code>Series&lt;SaveOp></code> can be retrieved from the supplier returned by
 * {@link TableSaver#save(DataFrame)}
 *
 * @see TableSaver#save(DataFrame)
 * @since 0.6
 */
public enum SaveOp {

    insert, update, skip
}
