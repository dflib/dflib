package com.nhl.dflib.jdbc;

import com.nhl.dflib.DataFrame;

/**
 * Defines possible outcomes for DataFrame rows during when saving via {@link com.nhl.dflib.jdbc.connector.TableSaver}.
 * A <code>Series&lt;SaveOp></code> can be retrieved from the supplier returned by
 * {@link com.nhl.dflib.jdbc.connector.TableSaver#save(DataFrame)}
 *
 * @see com.nhl.dflib.jdbc.connector.TableSaver#save(DataFrame)
 * @since 0.6
 */
public enum SaveOp {

    insert, update, skip
}
