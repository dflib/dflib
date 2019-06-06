package com.nhl.dflib.jdbc;

import com.nhl.dflib.DataFrame;

/**
 * Defines possible outcomes for DataFrame rows during save via {@link com.nhl.dflib.jdbc.connector.TableSaver}.
 *
 * @see com.nhl.dflib.jdbc.connector.TableSaver#save(DataFrame)
 * @since 0.6
 */
public enum SaveOp {

    insert, update, skip
}
